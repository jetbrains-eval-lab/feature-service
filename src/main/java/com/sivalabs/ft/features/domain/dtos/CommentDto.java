package com.sivalabs.ft.features.domain.dtos;

import java.time.Instant;
import java.util.List;

public record CommentDto(
    Long id,
    String text,
    Instant createdAt,
    String author,
    String featureCode,
    String releaseCode,
    Long parentId,
    List<CommentDto> replies
) {}