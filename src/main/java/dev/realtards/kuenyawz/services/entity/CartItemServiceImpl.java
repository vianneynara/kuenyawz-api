package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.entities.CartItem;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.mapper.CartItemMapper;
import dev.realtards.kuenyawz.mapper.ProductMapper;
import dev.realtards.kuenyawz.repositories.CartItemRepository;
import dev.realtards.kuenyawz.repositories.VariantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
	private final VariantRepository variantRepository;
	private final CartItemRepository cartItemRepository;
	private final CartItemMapper cartItemMapper;
	private final ProductMapper productMapper;

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
				.orElseThrow(()
						-> new EntityNotFoundException("Cart Item Not Found By Id: " + cartItemId));

		return convertToDto(cartItem);
	}

	@Override
	public List<CartItemDto> getCartItemsOfAccount(Long accountId) {
		List<CartItem> cartItems = cartItemRepository.findAllByAccountId(accountId);

		return cartItems.stream()
				.map(this::convertToDto)
				.toList();
	}

	@Override
	public Page<CartItemDto> getCartItemsOfAccount(Long accountId, PageRequest pageRequest) {
		Page<CartItem> cartItems = cartItemRepository.findAll(accountId, pageRequest);

		return cartItems.map(this::convertToDto);
	}

	@Override
	public CartItemDto createCartItem(Long accountId, CartItemPostDto cartItemPostDto) {
		validateCartItemDto(cartItemPostDto);

		CartItem cartItem = buildCartItem(cartItemPostDto);

		CartItem savedCartItem = cartItemRepository.save(cartItem);
		CartItemDto cartItemDto = convertToDto(savedCartItem);

		return cartItemDto;
	}

	@Override
	public CartItemDto patchCartItem(Long cartItemId, CartItemPatchDto cartItemPatchDto) {
		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new EntityNotFoundException("CartItem not found for ID: " + cartItemId));

		CartItem updatedCartItem = cartItemMapper.updateCartItemFromPatch(cartItem, cartItemPatchDto);
		CartItem savedCartItem = cartItemRepository.save(updatedCartItem);

		return convertToDto(savedCartItem);
	}

	@Override
	public boolean deleteCartItem(Long cartItemId) {
		Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);

		if (cartItemOptional.isPresent()) {
			cartItemRepository.delete(cartItemOptional.get());
			return true;
		}
		return false;
	}


	@Override
	public boolean deleteCartItemsOfAccount(Long accountId) {
		int count = cartItemRepository.countCartItemByAccountId(accountId);
		if (count == 0) {
			return false;
		}

		cartItemRepository.deleteByAccount_AccountId(accountId);
		return true;
	}


	private void validateCartItemDto(CartItemPostDto cartItemPostDto) {
		if (cartItemPostDto == null) {
			throw new InvalidRequestBodyValue("CartItemPostDto cannot be null");
		}
		if (variantRepository.findById(cartItemPostDto.getVariantId()).orElse(null) == null) {
			throw new EntityNotFoundException("Cart Item not found for ID: " + cartItemPostDto.getVariantId());
		}
	}

	private CartItem buildCartItem(CartItemPostDto cartItemPostDto) {
		Variant variant = variantRepository.findById(cartItemPostDto.getVariantId()).orElse(null);

		variant.getProduct().getProductId();

		CartItem cartItem = CartItem.builder()
				.variant(variant)
				.note(cartItemPostDto.getNote())
				.quantity(cartItemPostDto.getQuantity())
				.build();

		return cartItem;
	}

	private ProductDto getProductDto(CartItem cartItem) {
		Variant variant = variantRepository.findById(cartItem.getVariant().getVariantId()).orElse(null);

		Product productFinded = variant.getProduct();
		ProductDto productDto = productMapper.fromEntity(productFinded);

		return productDto;
	}

	public CartItemDto convertToDto(CartItem cartItem) {
		ProductDto productDto = getProductDto(cartItem);
		CartItemDto cartItemDto = cartItemMapper.fromEntity(cartItem, productDto);

		return cartItemDto;
	}

	@Override
	public boolean deleteCartItemOfUser(Long cartItemId, Long accountId) {
		return false;
	}
}
