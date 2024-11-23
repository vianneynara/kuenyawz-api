package dev.realtards.kuenyawz.services.logic;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.dtos.image.BatchImageUploadDto;
import dev.realtards.kuenyawz.dtos.image.ImageResourceDTO;
import dev.realtards.kuenyawz.dtos.image.ImageUploadDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.ProductImage;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import dev.realtards.kuenyawz.exceptions.ResourceUploadException;
import dev.realtards.kuenyawz.repositories.ProductImageRepository;
import dev.realtards.kuenyawz.repositories.ProductRepository;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {

	private final ProductRepository productRepository;

	private final ApplicationProperties properties;
	private final SnowFlakeIdGenerator idGenerator;
	private final ProductImageRepository productImageRepository;

	private final String ROOT_TO_UPLOAD = "/src/main/resources/uploads";
	private String productImagesDir = "product-images";
	private Path uploadLocation;
	private Set<String> acceptedExtensions;

	@Override
	@PostConstruct
	public void init() {
		productImagesDir = properties.getProductImagesDir();
		try {
			uploadLocation = Path.of(System.getProperty("user.dir"), ROOT_TO_UPLOAD, productImagesDir)
				.normalize()
				.toAbsolutePath();
			log.info("Upload directory set at '{}'", uploadLocation);
			acceptedExtensions = Set.copyOf(properties.getAcceptedImageExtensions());
			if (!Files.exists(uploadLocation)) {
				log.info("Creating upload directory at: {}", uploadLocation);
				Files.createDirectories(uploadLocation);
			}
		} catch (IOException e) {
			log.error("Failed to create upload directory at {}", uploadLocation, e);
			throw new ResourceUploadException("Could not create upload directory");
		} catch (SecurityException e) {
			log.error("Permission denied to create upload directory at {}", uploadLocation, e);
			throw new ResourceUploadException("Permission denied to create upload directory");
		}
	}

	@Override
	public ImageResourceDTO store(Long productId, ImageUploadDto imageUploadDto) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product " + productId + " not found"));
		if (product.getImages().size() >= 3) {
			throw new ResourceUploadException("Product " + productId + " has reached the maximum number of images");
		}

		ImageResourceDTO imageResourceDTO = processImageStoring(product, imageUploadDto);
		return imageResourceDTO;
	}

	@Override
	public List<ImageResourceDTO> batchStore(Long productId, BatchImageUploadDto batchImageUploadDto) {
		if (batchImageUploadDto == null || batchImageUploadDto.getFiles().isEmpty()) {
			throw new ResourceUploadException("Cannot store empty batch");
		}

		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product " + productId + " not found"));
		if ((product.getImages().size() + batchImageUploadDto.getFiles().size()) > 3) {
			throw new ResourceUploadException("Product " + productId + " already has " + product.getImages().size()
				+ " images, can only save " + (3 - product.getImages().size()) + " more images");
		}

		// For each MultiPartFile in the batch DTO, process the image storing
		List<ImageResourceDTO> imageResourceDTOs = new java.util.ArrayList<>(List.of());
		batchImageUploadDto.getFiles().forEach(file -> {
			ImageResourceDTO imageResourceDTO = processImageStoring(product, file);
			imageResourceDTOs.add(imageResourceDTO);
		});
		return imageResourceDTOs;
	}

	@Override
	public Resource loadAsResource(final Long productId, String resourceUri) {
		try {
			final long resourceId = Long.parseLong(resourceUri.split("\\.")[0]);

			String relativePath = productImageRepository.findByProduct_ProductIdAndProductImageId(productId, resourceId)
				.orElseThrow(() -> new ResourceNotFoundException("Resource '" + productId + "/" + resourceId + "' not found"))
				.getRelativePath();
			Path requestedPath = Path.of(uploadLocation.toString(), relativePath).normalize().toAbsolutePath();
			Resource resource = new UrlResource(requestedPath.toUri());

			if (resource.exists() || resource.isReadable()) {
				log.info("Resource '{}' found, exists and readable", requestedPath);
				return resource;
			} else {
				log.warn("Resource '{}' not found", requestedPath);
				throw new ResourceNotFoundException("Resource '" + productId + "/" + resourceUri + "' not found");
			}
		} catch (NumberFormatException | MalformedURLException e) {
			throw new ResourceNotFoundException("Resource '" + productId + "/" + resourceUri + "' not found");
		}
	}

	@Override
	public void delete(Long productId, String resourceUri) {
		try {
			final long resourceId = Long.parseLong(resourceUri.split("\\.")[0]);
			ProductImage productImage = productImageRepository.findByProduct_ProductIdAndProductImageId(productId, resourceId)
				.orElseThrow(() -> new ResourceNotFoundException("Resource '" + productId + "/" + resourceId + "' not found"));

			Path requestedPath = Path.of(uploadLocation.toString(), productImage.getRelativePath()).normalize().toAbsolutePath();
			Files.deleteIfExists(requestedPath);
			productImageRepository.delete(productImage);
		} catch (NumberFormatException | IOException e) {
			throw new ResourceNotFoundException("Resource '" + productId + "/" + resourceUri + "' not found");
		}
	}

	@Override
	public void deleteAllOfProductId(Long productId) {
		Path productDirectory = Paths.get(uploadLocation.toString(), productId.toString());
		try (Stream<Path> paths = Files.walk(productDirectory)) {
			paths
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.forEach(File::delete);
			Files.deleteIfExists(productDirectory);
		} catch (NoSuchFileException e) {
			log.warn("Upload directory of product {} does not exist", productId);
		} catch (IOException e) {
			log.warn("Failed to delete product directory for product {}", productId, e);
		} catch (SecurityException e) {
			log.error("Permission denied to delete product directory for product {}", productId, e);
			throw new ResourceUploadException("Permission denied to delete product directory for product " + productId);
		}
		productImageRepository.deleteAllByProduct_ProductId(productId);
	}

	@Override
	public void deleteAll() {
		try (Stream<Path> directories = Files.walk(uploadLocation)) {
			try (Stream<Path> paths = directories.filter(Files::isDirectory)) {
				paths
					.map(Path::toFile)
					.forEach((File file) -> {
						try (Stream<Path> files = Files.walk(file.toPath())) {
							files.map(Path::toFile).forEach(File::delete);
						} catch (IOException e) {
							log.error("Failed to delete directory {}", file, e);
						}
					});
			}
			Files.deleteIfExists(uploadLocation);
			Files.createDirectories(uploadLocation);
		} catch (NoSuchFileException e) {
			log.warn("Upload directory {} does not exist", uploadLocation);
		} catch (IOException e) {
			log.error("Failed to delete upload directory", e);
			throw new ResourceUploadException("Could not delete upload directory");
		} catch (SecurityException e) {
			log.error("Permission denied to delete upload directory", e);
			throw new ResourceUploadException("Permission denied to delete upload directory");
		}
		productImageRepository.deleteAll();
	}

	@Override
	public String getImageUrl(Long productId, String resourceUri) {
		return properties.getFullBaseUrl() + "/api/images/" + productId + "/" + resourceUri;
	}

	@Override
	public String getImageUrl(ProductImage productImage) {
		return getImageUrl(productImage.getProduct().getProductId(), productImage.getStoredFilename());
	}

	@Override
	public List<String> getImageUrls(Product product) {
		if (product.getImages() == null) {
			return List.of();
		}
		List<ProductImage> productImages = new ArrayList<>(product.getImages().stream().toList());
		productImages.sort(Comparator.comparing(ProductImage::getProductImageId));

		return productImages.stream()
			.map(this::getImageUrl)
			.toList();
	}

	// Helper / extracted methods

	/**
	 * Process image storing using multipart file to be converted to {@link ImageUploadDto}.
	 *
	 * @param product {@link Product}
	 * @param file {@link MultipartFile}
	 * @return {@link ImageResourceDTO}
	 */
	private ImageResourceDTO processImageStoring(Product product, MultipartFile file) {
		return processImageStoring(product, ImageUploadDto.builder().file(file).build());
	}

	/**
	 * {@link ImageUploadDto} storing procedure.
	 *
	 * @param product {@link Product}
	 * @param imageUploadDto {@link }
	 * @return {@link ImageResourceDTO}
	 */
	private ImageResourceDTO processImageStoring(Product product, ImageUploadDto imageUploadDto) {
		final MultipartFile file = imageUploadDto.getFile();

		if (file.isEmpty()) {
			throw new ResourceUploadException("Cannot store empty file");
		}

		final String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
		final String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

		if (!acceptedExtensions.contains(fileExtension.toLowerCase())) {
			throw new ResourceUploadException(String.format("Invalid file extension '%s', accepted: %s",
				fileExtension, String.join(", ", acceptedExtensions)));
		}

		final Long generatedId = idGenerator.generateId();
		final String storedFilename = generatedId + "." + fileExtension;
		final Path productDirectory = Paths.get(uploadLocation.toString(), product.getProductId().toString());

		if (!productDirectory.toFile().exists()) {
			try {
				Files.createDirectories(Paths.get(uploadLocation.toString(), product.getProductId().toString()));
			} catch (IOException e) {
				log.error("Failed to create directory for product {}", product.getProductId(), e);
				throw new ResourceUploadException("Could not create directory for product " + product.getProductId());
			}
		}

		final Path destinationPath = productDirectory.resolve(storedFilename).normalize();

		if (!destinationPath.startsWith(productDirectory)) {
			throw new ResourceUploadException("Cannot store file outside product upload directory");
		}

		try (InputStream inputStream = file.getInputStream()) {
			// Copy file to the target location
			Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);

			// Get safe relative path
			Path relativePath = uploadLocation.relativize(destinationPath);

			// Save to database
			productImageRepository.save(ProductImage.builder()
				.productImageId(generatedId)
				.originalFilename(originalFilename)
				.storedFilename(storedFilename)
				.relativePath(relativePath.toString())
				.fileSize(file.getSize())
				.product(product)
				.build());

			return ImageResourceDTO.builder()
				.imageResourceId(generatedId)
				.originalFilename(originalFilename)
				.filename(storedFilename)
				.relativeLocation(relativePath.toString())
				.build();

		} catch (IOException e) {
			log.error("Failed to store file {}: {}", originalFilename, e.getMessage());
			throw new ResourceUploadException("Failed to store file " + originalFilename);
		}
	}
}
