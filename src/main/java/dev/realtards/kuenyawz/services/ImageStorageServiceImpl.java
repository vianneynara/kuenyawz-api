package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.dtos.image.ImageResourceDTO;
import dev.realtards.kuenyawz.dtos.image.ImageUploadDto;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import dev.realtards.kuenyawz.exceptions.ResourceUploadException;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {

    private final ApplicationProperties applicationProperties;
    private final SnowFlakeIdGenerator idGenerator;

	private final String ROOT_TO_UPLOAD = "/src/main/resources/uploads";
	private String productImagesDir = "product-images";
	private Path uploadLocation;
	private Set<String> acceptedExtensions;

	@Override
	@PostConstruct
	public void init() {
		productImagesDir = applicationProperties.getProductImagesDir();
		try {
			uploadLocation = Path.of(System.getProperty("user.dir"), ROOT_TO_UPLOAD, productImagesDir)
				.normalize()
				.toAbsolutePath();
			log.info("Upload directory set at '{}'", uploadLocation);
			acceptedExtensions = Set.copyOf(applicationProperties.getAcceptedImageExtensions());
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
		if (product.getVariants().size() >= 3) {
			throw new ResourceUploadException("Product " + productId + " has reached the maximum number of images");
		}

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
		final Path productDirectory = Paths.get(uploadLocation.toString(), productId.toString());

		if (!productDirectory.toFile().exists()) {
			try {
				Files.createDirectories(Paths.get(uploadLocation.toString(), productId.toString()));
			} catch (IOException e) {
				log.error("Failed to create directory for product {}", productId, e);
				throw new ResourceUploadException("Could not create directory for product " + productId);
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

    @Override
    public Resource loadAsResource(String requestedPath) {
        try {
            Path filePath = uploadRootDir.resolve(requestedPath).normalize();

            if (!filePath.startsWith(uploadRootDir)) {
                throw new ResourceNotFoundException("Cannot access file outside upload directory");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found: " + requestedPath);
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found: " + requestedPath);
        }
    }

    @Override
    public void delete(Long resourceId) {
        try {
            Path directory = uploadRootDir;
            try (Stream<Path> files = Files.list(directory)) {
                Optional<Path> fileToDelete = files
                    .filter(file -> file.getFileName().toString().startsWith(resourceId.toString() + "."))
                    .findFirst();

                fileToDelete.ifPresent(path -> {
                    try {
                        Files.delete(path);
                        log.info("Deleted file: {}", path);
                    } catch (IOException e) {
                        log.error("Error deleting file: {}", path, e);
                        throw new ResourceUploadException("Failed to delete file");
                    }
                });
            }
        } catch (IOException e) {
            log.error("Error listing directory contents", e);
            throw new ResourceUploadException("Failed to access directory");
        }
    }

    @Override
    public void deleteAll() {
        try (Stream<Path> file = Files.walk(uploadRootDir)) {
            file.sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.error("Error deleting path: {}", path, e);
                    }
                });
            Files.createDirectories(uploadRootDir);
        } catch (IOException e) {
            log.error("Error deleting all files", e);
            throw new ResourceUploadException("Failed to delete all files");
        }
    }

    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(filename.lastIndexOf(".") + 1))
            .orElseThrow(() -> new ResourceUploadException("Invalid file format"));
    }
}