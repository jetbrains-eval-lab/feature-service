package com.sivalabs.ft.features.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.domain.models.DependencyType;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class FeatureDependencyTest {

    @Test
    void shouldCreateFeatureDependencyWithAllFields() {
        // Given
        String featureCode = "FEATURE-001";
        String dependsOnFeatureCode = "FEATURE-002";
        DependencyType dependencyType = DependencyType.HARD;
        String notes = "This is a hard dependency";
        Instant createdAt = Instant.now();

        // When
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(featureCode);
        dependency.setDependsOnFeatureCode(dependsOnFeatureCode);
        dependency.setDependencyType(dependencyType);
        dependency.setNotes(notes);
        dependency.setCreatedAt(createdAt);

        // Then
        assertThat(dependency.getFeatureCode()).isEqualTo(featureCode);
        assertThat(dependency.getDependsOnFeatureCode()).isEqualTo(dependsOnFeatureCode);
        assertThat(dependency.getDependencyType()).isEqualTo(dependencyType);
        assertThat(dependency.getNotes()).isEqualTo(notes);
        assertThat(dependency.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldCreateFeatureDependencyWithMinimalFields() {
        // Given
        String featureCode = "FEATURE-001";
        String dependsOnFeatureCode = "FEATURE-002";
        DependencyType dependencyType = DependencyType.SOFT;

        // When
        FeatureDependency dependency = new FeatureDependency();
        dependency.setFeatureCode(featureCode);
        dependency.setDependsOnFeatureCode(dependsOnFeatureCode);
        dependency.setDependencyType(dependencyType);

        // Then
        assertThat(dependency.getFeatureCode()).isEqualTo(featureCode);
        assertThat(dependency.getDependsOnFeatureCode()).isEqualTo(dependsOnFeatureCode);
        assertThat(dependency.getDependencyType()).isEqualTo(dependencyType);
        assertThat(dependency.getNotes()).isNull();
        assertThat(dependency.getId()).isNull();
    }

    @Test
    void shouldSetAndGetId() {
        // Given
        FeatureDependency dependency = new FeatureDependency();
        Long id = 1L;

        // When
        dependency.setId(id);

        // Then
        assertThat(dependency.getId()).isEqualTo(id);
    }

    @Test
    void shouldHandleAllDependencyTypes() {
        // Given
        FeatureDependency hardDependency = new FeatureDependency();
        FeatureDependency softDependency = new FeatureDependency();
        FeatureDependency optionalDependency = new FeatureDependency();

        // When
        hardDependency.setDependencyType(DependencyType.HARD);
        softDependency.setDependencyType(DependencyType.SOFT);
        optionalDependency.setDependencyType(DependencyType.OPTIONAL);

        // Then
        assertThat(hardDependency.getDependencyType()).isEqualTo(DependencyType.HARD);
        assertThat(softDependency.getDependencyType()).isEqualTo(DependencyType.SOFT);
        assertThat(optionalDependency.getDependencyType()).isEqualTo(DependencyType.OPTIONAL);
    }
}
