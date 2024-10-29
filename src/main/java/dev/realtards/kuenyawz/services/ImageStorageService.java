package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.image.ImageResourceDTO;
import dev.realtards.kuenyawz.dtos.image.ImageUploadDto;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

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
	ImageResourceDTO store(ImageUploadDto imageUploadDto, Long productId);

	@Transactional(readOnly = true)
	Resource loadAsResource(String filename);

	@Transactional
	void delete(Long resourceId);

	@Transactional
	void deleteAll();
}
