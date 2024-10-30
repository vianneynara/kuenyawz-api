package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.image.ImageResourceDTO;
import dev.realtards.kuenyawz.dtos.image.ImageUploadDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.ProductImage;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ImageStorageService {

	/**
	 * Initializes the storage service, this is important to locate or create the upload
	 * directory.
	 */
	void init();

	/**
	 * Prepares the storing mechanism with the file and the product id.
	 * @param imageUploadDto {@link ImageUploadDto}
	 * @param productId {@link Long} the product id to be associated with the file.
	 * @return {@link ImageResourceDTO}
	 */
	@Transactional
	ImageResourceDTO store(Long productId, ImageUploadDto imageUploadDto);

	/**
	 * Loads the resource as a {@link Resource} object by using product id and resource filename.
	 * @param productId {@link Long} the product id to be associated with the file.
	 * @param resourceUri {@link String} the resource uri.
	 * @return {@link Resource}
	 */
	@Transactional(readOnly = true)
	Resource loadAsResource(Long productId, String resourceUri);

	/**
	 * Deletes the resource by using product id and resource filename.
	 * @param productId {@link Long} the product id to be associated with the file.
	 * @param resourceUri {@link String} the resource uri.
	 */
	@Transactional
	void delete(Long productId, String resourceUri);

	/**
	 * Deletes all resources associated with the product id.
	 * @param productId {@link Long} the product id to be associated with the file.
	 */
	@Transactional
	void deleteAllOfProduct(Long productId);

	/**
	 * Deletes all resources.
	 */
	@Transactional
	void deleteAll();

	String getImageUrl(Long productId, String resourceUri);

	String getImageUrl(ProductImage productImage);

	List<String> getImageUrls(Product product);
}
