package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.domain.entities.FeatureDependency;
import com.sivalabs.ft.features.domain.models.DependencyType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

interface FeatureDependencyRepository extends ListCrudRepository<FeatureDependency, Long> {

    List<FeatureDependency> findByFeatureCode(String featureCode);

    List<FeatureDependency> findByDependsOnFeatureCode(String dependsOnFeatureCode);

    List<FeatureDependency> findByFeatureCodeAndDependencyType(String featureCode, DependencyType dependencyType);

    Optional<FeatureDependency> findByFeatureCodeAndDependsOnFeatureCode(
            String featureCode, String dependsOnFeatureCode);

    boolean existsByFeatureCodeAndDependsOnFeatureCode(String featureCode, String dependsOnFeatureCode);

    @Query(
            "SELECT fd FROM FeatureDependency fd WHERE fd.featureCode = :featureCode OR fd.dependsOnFeatureCode = :featureCode")
    List<FeatureDependency> findAllDependenciesForFeature(String featureCode);

    @Modifying
    @Query(
            "DELETE FROM FeatureDependency fd WHERE fd.featureCode = :featureCode OR fd.dependsOnFeatureCode = :featureCode")
    void deleteAllByFeatureCode(String featureCode);

    @Query("SELECT CASE WHEN COUNT(fd) > 0 THEN true ELSE false END FROM FeatureDependency fd "
            + "WHERE fd.featureCode = :featureCode OR fd.dependsOnFeatureCode = :dependsOnFeatureCode")
    boolean existsCircularDependency(String featureCode, String dependsOnFeatureCode);
}
