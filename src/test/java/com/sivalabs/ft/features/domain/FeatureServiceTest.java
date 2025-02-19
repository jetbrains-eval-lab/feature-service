package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.DatabaseConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(DatabaseConfiguration.class)
@Sql(scripts = {"/test-data.sql"})
class FeatureServiceTest {

    @Autowired
    FeatureService featureService;

    @MockitoBean("eventPublisher")
    EventPublisher eventPublisher;
    
    @BeforeEach
    public void setupMocks() {
        Mockito.reset(eventPublisher);
        Mockito.doNothing().when(eventPublisher).publishFeatureCreatedEvent(Mockito.any());
    }

    @Test
    void shouldGetFeaturesByReleaseCode() {
        List<Feature> features = featureService.findFeatures ("IJ-2023.3.8");
        assertThat(features).hasSize(2);
    }

    @Test
    void shouldGetFeatureByCode() {
        Optional<Feature> feature = featureService.findFeatureByCode("IJ-10001");
        assertThat(feature.isPresent()).isTrue();
        assertThat(feature.get().getCode()).isEqualTo("IJ-10001");
    }

    @Test
    void shouldCreateNewFeature() {
        CreateFeatureCommand request = new CreateFeatureCommand(
                "intellij",
                "IJ-2023.3.8",
                "IJ-999999",
                "New Feature",
                "New feature description",
                "john.doe",
                "jane.doe"
        );
        ArgumentCaptor<Feature> eventCaptor = ArgumentCaptor.forClass(Feature.class);
        Long featureId = featureService.createFeature(request);
        verify(eventPublisher).publishFeatureCreatedEvent(eventCaptor.capture());
        assertThat(featureId).isNotNull();
        assertThat(featureId).isGreaterThan(3L);
    }
}
