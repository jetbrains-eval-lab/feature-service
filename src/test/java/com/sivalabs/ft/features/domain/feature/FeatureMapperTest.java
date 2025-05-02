package com.sivalabs.ft.features.domain.feature;

import com.sivalabs.ft.features.DatabaseConfiguration;
import com.sivalabs.ft.features.api.dtos.FeatureDto;
import com.sivalabs.ft.features.mappers.FeatureMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DatabaseConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
@ComponentScan("com.sivalabs.ft.features.mappers")
public class FeatureMapperTest {

    @Autowired
    private FeatureMapper featureMapper;

    @Autowired
    private FeatureRepository featureRepository;

    @Test
    void shouldMapFeaturesWithProductDetails() {
        List<Feature> features = featureRepository.findByReleaseCode("GO-2024.2.3");
        List<FeatureDto> mappedFeatures =
                features.stream().map(featureMapper::toDto).toList();
        mappedFeatures.forEach(dto -> {
            assertThat(dto.product()).isNotNull();
            assertThat(dto.product().name()).isEqualTo("GoLand");
        });
    }
}
