package com.sivalabs.ft.features.domain.entities;

import com.sivalabs.ft.features.domain.models.DependencyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "feature_dependencies")
public class FeatureDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feature_dependencies_id_gen")
    @SequenceGenerator(
            name = "feature_dependencies_id_gen",
            sequenceName = "feature_dependency_id_seq",
            allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50) @NotNull @Column(name = "feature_code", nullable = false, length = 50)
    private String featureCode;

    @Size(max = 50) @NotNull @Column(name = "depends_on_feature_code", nullable = false, length = 50)
    private String dependsOnFeatureCode;

    @NotNull @Column(name = "dependency_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DependencyType dependencyType;

    @Column(name = "notes", length = 1000)
    private String notes;

    @NotNull @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public String getDependsOnFeatureCode() {
        return dependsOnFeatureCode;
    }

    public void setDependsOnFeatureCode(String dependsOnFeatureCode) {
        this.dependsOnFeatureCode = dependsOnFeatureCode;
    }

    public DependencyType getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(DependencyType dependencyType) {
        this.dependencyType = dependencyType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
