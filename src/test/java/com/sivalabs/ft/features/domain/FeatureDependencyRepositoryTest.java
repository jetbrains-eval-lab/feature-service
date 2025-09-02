package com.sivalabs.ft.features.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import com.sivalabs.ft.features.domain.entities.Feature;
import com.sivalabs.ft.features.domain.entities.FeatureDependency;
import com.sivalabs.ft.features.domain.entities.Product;
import com.sivalabs.ft.features.domain.models.DependencyType;
import com.sivalabs.ft.features.domain.models.FeatureStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class FeatureDependencyRepositoryTest {

    @Autowired
    private FeatureDependencyRepository featureDependencyRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Feature feature1;
    private Feature feature2;
    private Feature feature3;

    @BeforeEach
    void setUp() {
        // Get existing product or create one
        Product product = productRepository.findByCode("intellij").orElseGet(() -> {
            Product newProduct = new Product();
            newProduct.setCode("test-product");
            newProduct.setPrefix("TP");
            newProduct.setName("Test Product");
            newProduct.setImageUrl("http://example.com/image.png");
            newProduct.setCreatedBy("test-user");
            newProduct.setCreatedAt(Instant.now());
            return productRepository.save(newProduct);
        });

        // Create unique test features with timestamp to avoid duplicates
        long timestamp = System.currentTimeMillis();

        feature1 = featureRepository.findByCode("IDEA-358562").orElseGet(() -> {
            Feature newFeature = new Feature();
            newFeature.setCode("TEST-FEATURE-001-" + timestamp);
            newFeature.setTitle("Test Feature 1");
            newFeature.setStatus(FeatureStatus.NEW);
            newFeature.setProduct(product);
            newFeature.setCreatedBy("test-user");
            newFeature.setCreatedAt(Instant.now());
            return featureRepository.save(newFeature);
        });

        feature2 = featureRepository.findByCode("IDEA-360676").orElseGet(() -> {
            Feature newFeature = new Feature();
            newFeature.setCode("TEST-FEATURE-002-" + timestamp);
            newFeature.setTitle("Test Feature 2");
            newFeature.setStatus(FeatureStatus.NEW);
            newFeature.setProduct(product);
            newFeature.setCreatedBy("test-user");
            newFeature.setCreatedAt(Instant.now());
            return featureRepository.save(newFeature);
        });

        feature3 = featureRepository.findByCode("IDEA-352694").orElseGet(() -> {
            Feature newFeature = new Feature();
            newFeature.setCode("TEST-FEATURE-003-" + timestamp);
            newFeature.setTitle("Test Feature 3");
            newFeature.setStatus(FeatureStatus.NEW);
            newFeature.setProduct(product);
            newFeature.setCreatedBy("test-user");
            newFeature.setCreatedAt(Instant.now());
            return featureRepository.save(newFeature);
        });
    }

    @Test
    void shouldSaveAndRetrieveFeatureDependency() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode(feature2.getCode());
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setNotes("Critical dependency");
        dependency.setCreatedAt(Instant.now());

        // When
        FeatureDependency saved = featureDependencyRepository.save(dependency);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFeatureCode()).isEqualTo(feature1.getCode());
        assertThat(saved.getDependsOnFeatureCode()).isEqualTo(feature2.getCode());
        assertThat(saved.getDependencyType()).isEqualTo(DependencyType.HARD);
        assertThat(saved.getNotes()).isEqualTo("Critical dependency");
    }

    @Test
    void shouldFindDependenciesByFeatureCode() {
        // Given
        createTestDependency(feature1.getCode(), feature2.getCode(), DependencyType.HARD);
        createTestDependency(feature1.getCode(), feature3.getCode(), DependencyType.SOFT);

        // When
        List<FeatureDependency> dependencies = featureDependencyRepository.findByFeatureCode(feature1.getCode());

        // Then
        assertThat(dependencies).hasSize(2);
        assertThat(dependencies)
                .extracting(FeatureDependency::getDependsOnFeatureCode)
                .containsExactlyInAnyOrder(feature2.getCode(), feature3.getCode());
    }

    @Test
    void shouldFindDependenciesByDependsOnFeatureCode() {
        // Given
        createTestDependency(feature1.getCode(), feature3.getCode(), DependencyType.HARD);
        createTestDependency(feature2.getCode(), feature3.getCode(), DependencyType.SOFT);

        // When
        List<FeatureDependency> dependencies =
                featureDependencyRepository.findByDependsOnFeatureCode(feature3.getCode());

        // Then
        assertThat(dependencies).hasSize(2);
        assertThat(dependencies)
                .extracting(FeatureDependency::getFeatureCode)
                .containsExactlyInAnyOrder(feature1.getCode(), feature2.getCode());
    }

    @Test
    void shouldFindDependenciesByFeatureCodeAndDependencyType() {
        // Given
        createTestDependency(feature1.getCode(), feature2.getCode(), DependencyType.HARD);
        createTestDependency(feature1.getCode(), feature3.getCode(), DependencyType.SOFT);

        // When
        List<FeatureDependency> hardDependencies =
                featureDependencyRepository.findByFeatureCodeAndDependencyType(feature1.getCode(), DependencyType.HARD);

        // Then
        assertThat(hardDependencies).hasSize(1);
        assertThat(hardDependencies.get(0).getDependsOnFeatureCode()).isEqualTo(feature2.getCode());
    }

    @Test
    void shouldFindSpecificDependency() {
        // Given
        createTestDependency(feature1.getCode(), feature2.getCode(), DependencyType.HARD);

        // When
        Optional<FeatureDependency> dependency = featureDependencyRepository.findByFeatureCodeAndDependsOnFeatureCode(
                feature1.getCode(), feature2.getCode());

        // Then
        assertThat(dependency).isPresent();
        assertThat(dependency.get().getDependencyType()).isEqualTo(DependencyType.HARD);
    }

    @Test
    void shouldCheckIfDependencyExists() {
        // Given
        createTestDependency(feature1.getCode(), feature2.getCode(), DependencyType.HARD);

        // When & Then
        assertThat(featureDependencyRepository.existsByFeatureCodeAndDependsOnFeatureCode(
                        feature1.getCode(), feature2.getCode()))
                .isTrue();
        assertThat(featureDependencyRepository.existsByFeatureCodeAndDependsOnFeatureCode(
                        feature2.getCode(), feature1.getCode()))
                .isFalse();
    }

    @Test
    void shouldFindAllDependenciesForFeature() {
        // Given
        createTestDependency(feature1.getCode(), feature2.getCode(), DependencyType.HARD);
        createTestDependency(feature3.getCode(), feature1.getCode(), DependencyType.SOFT);

        // When
        List<FeatureDependency> allDependencies =
                featureDependencyRepository.findAllDependenciesForFeature(feature1.getCode());

        // Then
        assertThat(allDependencies).hasSize(2);
    }

    @Test
    void shouldDeleteDependency() {
        // Given
        FeatureDependency dependency =
                createTestDependency(feature1.getCode(), feature2.getCode(), DependencyType.HARD);

        // When
        featureDependencyRepository.deleteById(dependency.getId());

        // Then
        assertThat(featureDependencyRepository.findById(dependency.getId())).isEmpty();
    }

    @Test
    void shouldPreventDuplicateDependencies() {
        // Given
        createTestDependency(feature1.getCode(), feature2.getCode(), DependencyType.HARD);

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            FeatureDependency duplicate = new FeatureDependency();
            duplicate.setFeatureCode(feature1.getCode());
            duplicate.setDependsOnFeatureCode(feature2.getCode());
            duplicate.setDependencyType(DependencyType.SOFT);
            duplicate.setCreatedAt(Instant.now());
            featureDependencyRepository.save(duplicate);
            entityManager.flush();
        });
    }

    @Test
    void shouldPreventSelfDependency() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode(feature1.getCode());
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setCreatedAt(Instant.now());

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> {
            featureDependencyRepository.save(dependency);
            entityManager.flush();
        });
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldPreventInvalidFeatureCodeReference() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode("NON-EXISTENT");
        dependency.setDependsOnFeatureCode(feature2.getCode());
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setCreatedAt(Instant.now());

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            featureDependencyRepository.save(dependency);
            entityManager.flush(); // Force constraint check
        });
    }

    private FeatureDependency createTestDependency(
            String featureCode, String dependsOnFeatureCode, DependencyType type) {
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(featureCode);
        dependency.setDependsOnFeatureCode(dependsOnFeatureCode);
        dependency.setDependencyType(type);
        dependency.setCreatedAt(Instant.now());
        return featureDependencyRepository.save(dependency);
    }
}
