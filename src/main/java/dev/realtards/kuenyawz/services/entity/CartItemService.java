package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemService {
	/**
	 * Get all cart items as a list.
	 *
	 * @return {@link List} of {@link CartItemDto}
	 */
	@Transactional(readOnly = true)
	List<CartItemDto> getAllCartItems();

	/**
	 * Get all cart items as a list with pagination.
	 *
	 * @param pageRequest {@link PageRequest} pagination request
	 * @return {@link Page} of {@link CartItemDto}
	 */
	@Transactional(readOnly = true)
	Page<CartItemDto> getAllCartItems(PageRequest pageRequest);

	/**
	 * Get a cart item by its ID.
	 *
	 * @param cartItemId {@link Long} the cart item ID
	 * @return {@link CartItemDto}
	 */
	@Transactional(readOnly = true)
	CartItemDto getCartItem(Long cartItemId);

	/**
	 * Get all cart items of a user by its ID.
	 *
	 * @param accountId {@link Long} the account ID
	 * @return {@link List} of {@link CartItemDto}
	 */
	@Transactional(readOnly = true)
	List<CartItemDto> getCartItemsOfAccount(Long accountId);

	/**
	 * Get paginated cart items of a user by its ID.
	 *
	 * @param accountId   {@link Long} the account ID
	 * @param pageRequest {@link PageRequest} pagination request
	 * @return {@link Page} of {@link CartItemDto}
	 */
	@Transactional(readOnly = true)
	Page<CartItemDto> getCartItemsOfAccount(Long accountId, PageRequest pageRequest);

	/**
	 * Create a new cart item.
	 *
	 * @param accountId       {@link Long} the account ID
	 * @param cartItemPostDto {@link CartItemPostDto} the cart item information
	 * @return {@link CartItemDto}
	 */
	@Transactional
	CartItemDto createCartItem(Long accountId, CartItemPostDto cartItemPostDto);

	/**
	 * Update a cart item by its ID.
	 *
	 * @param cartItemId       {@link Long} the cart item ID
	 * @param cartItemPatchDto {@link CartItemPatchDto} the cart item patch information
	 * @param accountId
	 * @return {@link CartItemDto}
	 */
	@Transactional
	CartItemDto patchCartItem(Long cartItemId, CartItemPatchDto cartItemPatchDto, Long accountId);

	/**
	 * Delete a cart item by its ID.
	 *
	 * @param cartItemId {@link Long} the cart item ID
	 * @return {@link Boolean} whether the deletion is successful
	 */
	@Transactional
	boolean deleteCartItem(Long cartItemId);

	/**
	 * Delete all cart items of a user by its ID.
	 *
	 * @param accountId {@link Long} the account ID
	 * @return {@link Boolean} whether the deletion is successful
	 */
	@Transactional
	boolean deleteCartItemsOfAccount(Long accountId);
  
	/**
	 * Delete a cart item of a user by its ID. This method is used to prevent
	 * unauthorized deletion.
	 *
	 * @param cartItemId {@link Long} the cart item ID
	 * @param accountId  {@link Long} the account ID
	 * @return {@link Boolean} whether the deletion is successful
	 */
	@Transactional
	boolean deleteCartItemOfUser(Long cartItemId, Long accountId);
}
