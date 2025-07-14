package com.sivalabs.ft.features.domain.mappers;

import com.sivalabs.ft.features.domain.dtos.CommentDto;
import com.sivalabs.ft.features.domain.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "featureCode", source = "feature.code")
    @Mapping(target = "releaseCode", source = "release.code")
    @Mapping(target = "parentId", source = "parentComment.id")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> comments);
}