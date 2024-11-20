package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VariantRepository variantRepository;

    private Product product;
    private Variant variant1;
    private Variant variant2;

    @BeforeEach
    void setUp() {
        // Create a product
        product = Product.builder()
            .name("Test Muffin")
            .tagline("Delicious muffin")
            .description("A test muffin description")
            .category(Product.Category.CAKE)
            .available(true)
            .deleted(false)
            .build();
        
        // Save the product first
        entityManager.persist(product);

        // Create variants
        variant1 = Variant.builder()
            .price(new BigDecimal("29.99"))
            .type("Regular")
            .product(product)
            .minQuantity(1)
            .maxQuantity(10)
            .build();

        variant2 = Variant.builder()
            .price(new BigDecimal("39.99"))
            .type("(Addon) Extra Almonds")
            .product(product)
            .minQuantity(1)
            .maxQuantity(10)
            .build();

        // Save variants
        entityManager.persist(variant1);
        entityManager.persist(variant2);

        // Flush to ensure all entities are saved
        entityManager.flush();
    }

    @Test
    void findByProduct_ProductId_ShouldReturnVariant() {
        // when
        Optional<Variant> foundVariant = variantRepository.findFirstByProduct_ProductId(product.getProductId());

        // then
        assertThat(foundVariant).isPresent();
        assertThat(foundVariant.get().getProduct().getProductId()).isEqualTo(product.getProductId());
    }

    @Test
    void findByProduct_ProductId_ShouldReturnEmptyWhenProductNotFound() {
        // when
        Optional<Variant> foundVariant = variantRepository.findFirstByProduct_ProductId(999999L);

        // then
        assertThat(foundVariant).isEmpty();
    }

    @Test
    void findAllByProduct_ProductId_ShouldReturnVariants() {
        // when
        List<Variant> variants = variantRepository.findAllByProduct_ProductId(product.getProductId());

        // then
        assertThat(variants).hasSize(2);
        assertThat(variants).extracting(Variant::getProduct).allMatch(p -> p.getProductId().equals(product.getProductId()));
    }

    @Test
    void findAllByProduct_ProductId_ShouldReturnEmptyWhenProductNotFound() {
        // when
        List<Variant> variants = variantRepository.findAllByProduct_ProductId(999999L);

        // then
        assertThat(variants).isEmpty();
    }

    @Test
    void countVariantsByProduct_ProductId_ShouldReturnCorrectCount() {
        // when
        int count = variantRepository.countVariantsByProduct_ProductId(product.getProductId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void countVariantsByProduct_ProductId_ShouldReturnZeroWhenProductNotFound() {
        // when
        int count = variantRepository.countVariantsByProduct_ProductId(999999L);

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    void deleteByVariantIdAndProduct_ProductId_ShouldDeleteVariant() {
        // when
        int deletedCount = variantRepository.deleteByVariantIdAndProduct_ProductId(
            variant1.getVariantId(),
            product.getProductId()
        );

        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(deletedCount).isEqualTo(1);
        assertThat(entityManager.find(Variant.class, variant1.getVariantId())).isNull();
    }

    @Test
    void deleteByVariantIdAndProduct_ProductId_ShouldReturnZeroWhenNotFound() {
        // when
        int deletedCount = variantRepository.deleteByVariantIdAndProduct_ProductId(999999L, product.getProductId());

        // then
        assertThat(deletedCount).isEqualTo(0);
    }
}