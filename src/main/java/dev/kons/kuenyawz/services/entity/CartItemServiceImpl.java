package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.cartItem.CartItemDto;
import dev.kons.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.kons.kuenyawz.dtos.cartItem.CartItemPostDto;
import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.entities.CartItem;
import dev.kons.kuenyawz.entities.Product;
import dev.kons.kuenyawz.entities.Variant;
import dev.kons.kuenyawz.exceptions.IllegalOperationException;
import dev.kons.kuenyawz.exceptions.ResourceExistsException;
import dev.kons.kuenyawz.mapper.CartItemMapper;
import dev.kons.kuenyawz.mapper.ProductMapper;
import dev.kons.kuenyawz.repositories.CartItemRepository;
import dev.kons.kuenyawz.repositories.CartItemSpec;
import dev.kons.kuenyawz.repositories.VariantRepository;
import dev.kons.kuenyawz.services.logic.ImageStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
	private final VariantRepository variantRepository;
	private final CartItemRepository cartItemRepository;
	private final CartItemMapper cartItemMapper;
	private final ProductMapper productMapper;
	private final ImageStorageService imageStorageService;

	@Override
	public List<CartItemDto> getAllCartItems() {
		List<CartItem> cartItems = cartItemRepository.findAll();
		return cartItems.stream()
			.map(this::convertToDto)
			.toList();
	}

	@Override
	public Page<CartItemDto> getAllCartItems(PageRequest pageRequest) {
		Page<CartItem> cartItems = cartItemRepository.findAll(pageRequest);

		return cartItems.map(this::convertToDto);
	}

	@Override
	public CartItemDto getCartItem(Long cartItemId) {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
			.orElseThrow(() -> new EntityNotFoundException("Cart Item Not Found By Id: " + cartItemId));

		return convertToDto(cartItem);
	}

	@Override
	public List<CartItemDto> getCartItemsOfAccount(Long accountId) {
		List<CartItem> cartItems = cartItemRepository.findAllByAccount_AccountId(accountId);

		return cartItems.stream()
			.map(this::convertToDto)
			.toList();
	}

	@Override
	public Page<CartItemDto> getCartItemsOfAccount(Long accountId, PageRequest pageRequest) {
		Page<CartItem> cartItems = cartItemRepository.findAllByAccount_AccountId(accountId, pageRequest);

		return cartItems.map(this::convertToDto);
	}

	@Override
	public CartItemDto createCartItem(Long accountId, CartItemPostDto cartItemPostDto) {
		validateVariantExists(cartItemPostDto.getVariantId());
		validateNoSameProductInAccountCart(cartItemPostDto.getVariantId());

		CartItem cartItem = toEntity(cartItemPostDto);

		CartItem savedCartItem = cartItemRepository.save(cartItem);
		CartItemDto cartItemDto = convertToDto(savedCartItem);

		return cartItemDto;
	}

	@Override
	public CartItemDto patchCartItem(Long cartItemId, CartItemPatchDto cartItemPatchDto, Long accountId) {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
			.orElseThrow(() -> new EntityNotFoundException("CartItem not found for ID: " + cartItemId));
		Variant newVariant = cartItem.getVariant();

		// Prevent necessary validation when the variant id is changed (skipped if the variant id is unchanged)
		if (cartItemPatchDto.getVariantId() != null
			&& !cartItemPatchDto.getVariantId().equals(cartItem.getVariant().getVariantId())
		) {
			newVariant = variantRepository.findById(cartItemPatchDto.getVariantId())
				.orElseThrow(() -> new EntityNotFoundException("Variant not found with ID: " + cartItemPatchDto.getVariantId()));

			// Compares whether the variant's of the same product as the cart item's variant's product
			if (!newVariant.getProduct().getProductId().equals(cartItem.getVariant().getProduct().getProductId())) {
				throw new IllegalOperationException("Variant not found for the product in this cart item");
			}
		}
		if (cartItemPatchDto.getQuantity() != null
			&& cartItemPatchDto.getQuantity() >= newVariant.getMinQuantity()
			&& cartItemPatchDto.getQuantity() <= newVariant.getMaxQuantity()
		) {
			throw new IllegalOperationException("Quantity must be between " + newVariant.getMinQuantity() + " and " + newVariant.getMaxQuantity());
		}

		cartItem.patchFromDto(cartItemPatchDto, newVariant);
		CartItem savedCartItem = cartItemRepository.save(cartItem);

		return convertToDto(savedCartItem);
	}

	@Override
	public void deleteCartItem(Long cartItemId) {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
			.orElseThrow(() -> new EntityNotFoundException("CartItem not found for ID: " + cartItemId));

		cartItemRepository.deleteById(cartItem.getCartItemId());
	}

	@Override
	public boolean deleteCartItemsOfAccount(Long accountId) {
		int affected = cartItemRepository.deleteByAccount_AccountId(accountId);
		return affected > 0;
	}

	@Override
	public boolean deleteCartItemOfAccount(Long cartItemId, Long accountId) {
		int affected = cartItemRepository.deleteByCartItemIdAndAccount_AccountId(cartItemId, accountId);
		return affected > 0;
	}

	public CartItemDto convertToDto(CartItem cartItem) {
		Product product = cartItem.getVariant().getProduct();
		ProductDto productDto = productMapper.fromEntity(product);
		productDto.setImages(imageStorageService.getImageUrls(product));

		CartItemDto cartItemDto = cartItemMapper.fromEntity(cartItem, productDto, cartItem.getVariant().getVariantId());
		return cartItemDto;
	}

	private CartItem toEntity(CartItemPostDto cartItemPostDto) {
		Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Variant variant = variantRepository.findById(cartItemPostDto.getVariantId())
			.orElseThrow(() -> new EntityNotFoundException("Variant not found with ID: " + cartItemPostDto.getVariantId()));

		CartItem cartItem = CartItem.builder()
			.variant(variant)
			.note(cartItemPostDto.getNote())
			.quantity(cartItemPostDto.getQuantity())
			.account(account)
			.build();

		return cartItem;
	}

	private void validateVariantExists(Long variantId) {
		variantRepository.findById(variantId)
			.orElseThrow(() -> new EntityNotFoundException("Variant not found with ID: " + variantId));
	}

	private void validateNoSameProductInAccountCart(Long variantId) {
		Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// Scalable approach using JPA Specification
		boolean exists = cartItemRepository.exists(
			CartItemSpec.withSameProductAsVariantIdAndAccountId(variantId, account.getAccountId())
		);
		if (exists) {
			throw new ResourceExistsException("Cart Item with the same product as the variant ID already exists");
		}
	}
}
