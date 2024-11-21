package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CartItemService {
	/**
	 * Get all cart items as a list.
	 *
	 * @return {@link List} of {@link CartItemDto}
	 */
	List<CartItemDto> getAllCartItems();

	/**
	 * Get all cart items as a list with pagination.
	 *
	 * @param pageRequest {@link PageRequest} pagination request
	 * @return {@link Page} of {@link CartItemDto}
	 */
	Page<CartItemDto> getAllCartItems(PageRequest pageRequest);

	/**
	 * Get a cart item by its ID.
	 *
	 * @param cartItemId {@link Long} the cart item ID
	 * @return {@link CartItemDto}
	 */
	CartItemDto getCartItem(Long cartItemId);

	/**
	 * Get all cart items of a user by its ID.
	 *
	 * @param accountId {@link Long} the account ID
	 * @return {@link List} of {@link CartItemDto}
	 */
	List<CartItemDto> getCartItemsOfUser(Long accountId);

	/**
	 * Get paginated cart items of a user by its ID.
	 *
	 * @param accountId   {@link Long} the account ID
	 * @param pageRequest {@link PageRequest} pagination request
	 * @return {@link Page} of {@link CartItemDto}
	 */
	Page<CartItemDto> getCartItemsOfUser(Long accountId, PageRequest pageRequest);

	/**
	 * Create a new cart item.
	 *
	 * @param accountId       {@link Long} the account ID
	 * @param cartItemPostDto {@link CartItemPostDto} the cart item information
	 * @return {@link CartItemDto}
	 */
	CartItemDto createCartItem(Long accountId, CartItemPostDto cartItemPostDto);

	/**
	 * Update a cart item by its ID.
	 *
	 * @param cartItemId      {@link Long} the cart item ID
	 * @param cartItemPatchDto {@link CartItemPatchDto} the cart item patch information
	 * @return {@link CartItemDto}
	 */
	CartItemDto patchCartItem(Long cartItemId, CartItemPatchDto cartItemPatchDto);

	/**
	 * Delete a cart item by its ID.
	 *
	 * @param cartItemId {@link Long} the cart item ID
	 * @return {@link Boolean} whether the deletion is successful
	 */
	boolean deleteCartItem(Long cartItemId);

	/**
	 * Delete all cart items of a user by its ID.
	 *
	 * @param accountId {@link Long} the account ID
	 * @return {@link Boolean} whether the deletion is successful
	 */
	boolean deleteCartItemsOfUser(Long accountId);
}
