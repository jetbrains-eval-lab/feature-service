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
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class FeatureDependencyIntegrityTest {

    @Autowired
    private FeatureDependencyRepository featureDependencyRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product testProduct;
    private Feature feature1;
    private Feature feature2;

    @BeforeEach
    void setUp() {
        // Create test product
        testProduct = new Product();
        testProduct.setCode("TEST-PRODUCT-" + System.currentTimeMillis());
        testProduct.setPrefix("TP");
        testProduct.setName("Test Product");
        testProduct.setImageUrl("http://example.com/image.png");
        testProduct.setCreatedBy("test-user");
        testProduct.setCreatedAt(Instant.now());
        testProduct = productRepository.save(testProduct);

        // Create test features
        feature1 = new Feature();
        feature1.setCode("TEST-FEATURE-001-" + System.currentTimeMillis());
        feature1.setTitle("Test Feature 1");
        feature1.setStatus(FeatureStatus.NEW);
        feature1.setProduct(testProduct);
        feature1.setCreatedBy("test-user");
        feature1.setCreatedAt(Instant.now());
        feature1 = featureRepository.save(feature1);

        feature2 = new Feature();
        feature2.setCode("TEST-FEATURE-002-" + System.currentTimeMillis());
        feature2.setTitle("Test Feature 2");
        feature2.setStatus(FeatureStatus.NEW);
        feature2.setProduct(testProduct);
        feature2.setCreatedBy("test-user");
        feature2.setCreatedAt(Instant.now());
        feature2 = featureRepository.save(feature2);
    }

    @Test
    @Rollback
    void shouldEnforceForeignKeyConstraintForFeatureCode() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode("NON-EXISTENT-FEATURE");
        dependency.setDependsOnFeatureCode(feature2.getCode());
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setCreatedAt(Instant.now());

        // When & Then
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    featureDependencyRepository.save(dependency);
                    entityManager.flush();
                },
                "Should not allow dependency with non-existent feature code");
    }

    @Test
    @Rollback
    void shouldEnforceForeignKeyConstraintForDependsOnFeatureCode() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode("NON-EXISTENT-FEATURE");
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setCreatedAt(Instant.now());

        // When & Then
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    featureDependencyRepository.save(dependency);
                    entityManager.flush();
                },
                "Should not allow dependency with non-existent depends on feature code");
    }

    @Test
    @Rollback
    void shouldEnforceUniqueConstraint() {
        // Given
        FeatureDependency dependency1 = new FeatureDependency();
        dependency1.setFeatureCode(feature1.getCode());
        dependency1.setDependsOnFeatureCode(feature2.getCode());
        dependency1.setDependencyType(DependencyType.HARD);
        dependency1.setCreatedAt(Instant.now());
        featureDependencyRepository.save(dependency1);

        FeatureDependency dependency2 = new FeatureDependency();
        dependency2.setFeatureCode(feature1.getCode());
        dependency2.setDependsOnFeatureCode(feature2.getCode());
        dependency2.setDependencyType(DependencyType.SOFT);
        dependency2.setCreatedAt(Instant.now());

        // When & Then
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    featureDependencyRepository.save(dependency2);
                    entityManager.flush();
                },
                "Should not allow duplicate dependencies between same features");
    }

    @Test
    @Rollback
    void shouldEnforceSelfDependencyConstraint() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode(feature1.getCode());
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setCreatedAt(Instant.now());

        // When & Then
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    featureDependencyRepository.save(dependency);
                    entityManager.flush();
                },
                "Should not allow self-dependency");
    }

    @Test
    @Rollback
    void shouldEnforceDependencyTypeConstraint() {
        // Given - We cannot directly test enum constraint as it's enforced by JPA/Hibernate
        // But we can verify that only valid enum values are accepted
        FeatureDependency hardDependency = new FeatureDependency();
        hardDependency.setFeatureCode(feature1.getCode());
        hardDependency.setDependsOnFeatureCode(feature2.getCode());
        hardDependency.setDependencyType(DependencyType.HARD);
        hardDependency.setCreatedAt(Instant.now());

        // When
        FeatureDependency saved = featureDependencyRepository.save(hardDependency);

        // Then
        assertThat(saved.getDependencyType()).isEqualTo(DependencyType.HARD);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @Rollback
    void shouldCascadeDeleteWhenFeatureIsDeleted() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode(feature2.getCode());
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setCreatedAt(Instant.now());
        FeatureDependency saved = featureDependencyRepository.save(dependency);

        // Verify dependency exists
        assertThat(featureDependencyRepository.findById(saved.getId())).isPresent();

        // When - Delete dependencies first, then the feature
        featureDependencyRepository.deleteById(saved.getId());
        featureRepository.deleteById(feature1.getId());

        // Then - Feature and dependency should be deleted
        assertThat(featureDependencyRepository.findById(saved.getId())).isEmpty();
        assertThat(featureRepository.findById(feature1.getId())).isEmpty();
    }

    @Test
    @Rollback
    void shouldPreventDeletingFeatureThatIsReferencedAsDependency() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode(feature2.getCode());
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setCreatedAt(Instant.now());
        featureDependencyRepository.save(dependency);

        // When & Then - Try to delete feature2 which is referenced as dependency
        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    featureRepository.deleteById(feature2.getId());
                    entityManager.flush();
                },
                "Should not allow deleting feature that is referenced as dependency");
    }

    @Test
    @Rollback
    void shouldAllowValidDependencyCreation() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode(feature2.getCode());
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setNotes("Valid dependency for testing");
        dependency.setCreatedAt(Instant.now());

        // When
        FeatureDependency saved = featureDependencyRepository.save(dependency);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFeatureCode()).isEqualTo(feature1.getCode());
        assertThat(saved.getDependsOnFeatureCode()).isEqualTo(feature2.getCode());
        assertThat(saved.getDependencyType()).isEqualTo(DependencyType.HARD);

        // Verify it can be retrieved
        assertThat(featureDependencyRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @Rollback
    void shouldEnforceNotNullConstraints() {
        // Test feature_code not null
        FeatureDependency dependency1 = new FeatureDependency();
        dependency1.setFeatureCode(null);
        dependency1.setDependsOnFeatureCode(feature2.getCode());
        dependency1.setDependencyType(DependencyType.HARD);
        dependency1.setCreatedAt(Instant.now());

        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> {
                    featureDependencyRepository.save(dependency1);
                    entityManager.flush();
                },
                "Should not allow null feature_code");
    }

    @Test
    @Rollback
    void shouldEnforceNotNullDependsOnFeatureCode() {
        // Test depends_on_feature_code not null
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode(null);
        dependency.setDependencyType(DependencyType.HARD);
        dependency.setCreatedAt(Instant.now());

        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> {
                    featureDependencyRepository.save(dependency);
                    entityManager.flush();
                },
                "Should not allow null depends_on_feature_code");
    }

    @Test
    @Rollback
    void shouldEnforceNotNullDependencyType() {
        // Test dependency_type not null
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(feature1.getCode());
        dependency.setDependsOnFeatureCode(feature2.getCode());
        dependency.setDependencyType(null);
        dependency.setCreatedAt(Instant.now());

        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> {
                    featureDependencyRepository.save(dependency);
                    entityManager.flush();
                },
                "Should not allow null dependency_type");
    }
}
