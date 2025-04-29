package com.sivalabs.ft.features.mappers;

import com.sivalabs.ft.features.api.dtos.FeatureDto;
import com.sivalabs.ft.features.domain.feature.Feature;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeatureMapper {
    FeatureDto toDto(Feature feature);
}
