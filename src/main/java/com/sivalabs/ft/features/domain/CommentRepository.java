package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.domain.entities.Comment;
import com.sivalabs.ft.features.domain.entities.Feature;
import com.sivalabs.ft.features.domain.entities.Release;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends ListCrudRepository<Comment, Long> {
    List<Comment> findByFeature(Feature feature);
    List<Comment> findByRelease(Release release);
    List<Comment> findByAuthor(String author);
    Optional<Comment> findByIdAndAuthor(Long id, String author);
}