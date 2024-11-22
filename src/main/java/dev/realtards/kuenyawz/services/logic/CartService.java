package dev.realtards.kuenyawz.services.logic;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CartService {
	/**
	 * Get all cart items of authenticated user.
	 *
	 * @return {@link List} of {@link CartItemDto}
	 */
	List<CartItemDto> getCartItems();

	/**
	 * Get paginated cart items of authenticated user.
	 *
	 * @param page {@link Integer} requested page
	 * @param pageSize {@link Integer} requested page size
	 * @return {@link Page} of {@link CartItemDto}
	 */
	Page<CartItemDto> getCartItems(Integer page, Integer pageSize);

	/**
	 * Add a cart item to the authenticated user's cart.
	 *
	 * @param cartItemPostDto {@link CartItemPostDto}
	 * @return {@link CartItemDto}
	 */
	CartItemDto addCartItem(CartItemPostDto cartItemPostDto);

	/**
	 * Edit a cart item in the authenticated user's cart.
	 *
	 * @param cartItemId {@link Long} the cart item id
	 * @param cartItemPatchDto {@link CartItemPatchDto}
	 * @return {@link CartItemDto}
	 */
	CartItemDto editCartItem(Long cartItemId, CartItemPatchDto cartItemPatchDto);

	/**
	 * Delete a cart item in the authenticated user's cart.
	 *
	 * @param cartItemId {@link Long} the cart item id
	 * @return {@link Boolean} true if successful
	 */
	boolean deleteCartItem(Long cartItemId);

	/**
	 * Delete all cart items in the authenticated user's cart.
	 *
	 * @return {@link Boolean} true if successful
	 */
	boolean deleteCartItems();
}
