package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class CartItemServiceImpl implements CartItemService {
	@Override
	public List<CartItemDto> getAllCartItems() {
		return List.of();
	}

	@Override
	public Page<CartItemDto> getAllCartItems(PageRequest pageRequest) {
		return null;
	}

	@Override
	public CartItemDto getCartItem(Long cartItemId) {
		return null;
	}

	@Override
	public List<CartItemDto> getCartItemsOfUser(Long accountId) {
		return List.of();
	}

	@Override
	public Page<CartItemDto> getCartItemsOfUser(Long accountId, PageRequest pageRequest) {
		return null;
	}

	@Override
	public CartItemDto createCartItem(Long accountId, CartItemPostDto cartItemPostDto) {
		return null;
	}

	@Override
	public CartItemDto patchCartItem(Long cartItemId, CartItemPatchDto cartItemPatchDto) {
		return null;
	}

	@Override
	public boolean deleteCartItem(Long cartItemId) {
		return false;
	}

	@Override
	public boolean deleteCartItemsOfUser(Long accountId) {
		return false;
	}
}
