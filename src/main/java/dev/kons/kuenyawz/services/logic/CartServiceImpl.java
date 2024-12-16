package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.cartItem.CartItemDto;
import dev.kons.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.kons.kuenyawz.dtos.cartItem.CartItemPostDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.services.entity.CartItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartItemService cartItemService;

	@Override
	public List<CartItemDto> getCartItems() {
		Account account = getAccountOrThrow();

		return cartItemService.getCartItemsOfAccount(account.getAccountId());
	}

	@Override
	public Page<CartItemDto> getCartItems(Integer page, Integer pageSize) {
		page = (page == null || page < 1) ? 0 : page;
		pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;

		Account account = getAccountOrThrow();
		PageRequest pageRequest = PageRequest.of(page, pageSize);

		return cartItemService.getCartItemsOfAccount(account.getAccountId(), pageRequest);
	}

	@Override
	public CartItemDto addCartItem(CartItemPostDto cartItemPostDto) {
		Account account = getAccountOrThrow();

		return cartItemService.createCartItem(account.getAccountId(), cartItemPostDto);
	}

	@Override
	public CartItemDto editCartItem(Long cartItemId, CartItemPatchDto cartItemPatchDto) {
		Account account = getAccountOrThrow();

		return cartItemService.patchCartItem(cartItemId, cartItemPatchDto, account.getAccountId());
	}

	@Override
	public boolean deleteCartItem(Long cartItemId) {
		Account account = getAccountOrThrow();

		return cartItemService.deleteCartItemOfAccount(cartItemId, account.getAccountId());
	}

	@Override
	public boolean deleteCartItems() {
		Account account = getAccountOrThrow();

		return cartItemService.deleteCartItemsOfAccount(account.getAccountId());
	}

	Account getAccountOrThrow() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof Account) {
			return (Account) principal;
		}
		throw new EntityNotFoundException("Account not found");
	}
}
