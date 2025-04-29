package com.sivalabs.ft.features.mappers;

import com.sivalabs.ft.features.api.dtos.ReleaseDto;
import com.sivalabs.ft.features.domain.release.Release;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReleaseMapper {
    ReleaseDto toDto(Release release);
}
