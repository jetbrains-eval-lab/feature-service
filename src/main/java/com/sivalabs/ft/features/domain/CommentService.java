package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.domain.dtos.CommentDto;
import com.sivalabs.ft.features.domain.entities.Comment;
import com.sivalabs.ft.features.domain.entities.Feature;
import com.sivalabs.ft.features.domain.entities.Release;
import com.sivalabs.ft.features.domain.mappers.CommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final FeatureRepository featureRepository;
    private final ReleaseRepository releaseRepository;
    private final CommentMapper commentMapper;

    public CommentService(
            CommentRepository commentRepository,
            FeatureRepository featureRepository,
            ReleaseRepository releaseRepository,
            CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.featureRepository = featureRepository;
        this.releaseRepository = releaseRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public Long createComment(CreateCommentCommand cmd) {
        Comment comment = new Comment();
        comment.setText(cmd.text());
        comment.setAuthor(cmd.author());
        comment.setCreatedAt(Instant.now());

        if (cmd.featureCode() != null && cmd.releaseCode() != null) {
            Optional<Feature> feature = featureRepository.findByCode(cmd.featureCode());
            feature.ifPresent(comment::setFeature);

            Optional<Release> release = releaseRepository.findByCode(cmd.releaseCode());
            release.ifPresent(comment::setRelease);
        }

        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }

    @Transactional
    public Long addReply(AddReplyCommand cmd) {
        Optional<Comment> parentComment = commentRepository.findById(cmd.parentId());
        if (parentComment.isEmpty()) {
            throw new IllegalArgumentException("Parent comment not found");
        }

        Comment reply = new Comment();
        reply.setText(cmd.text());
        reply.setAuthor(cmd.author());
        reply.setCreatedAt(Instant.now());
        reply.setParentComment(parentComment.get());

        // Set the same feature/release as the parent comment
        reply.setFeature(parentComment.get().getFeature());
        reply.setRelease(parentComment.get().getRelease());

        Comment savedReply = commentRepository.save(reply);
        return savedReply.getId();
    }

    @Transactional(readOnly = true)
    public List<CommentDto> findCommentsByFeature(String featureCode) {
        Optional<Feature> feature = featureRepository.findByCode(featureCode);
        if (feature.isEmpty()) {
            return List.of();
        }
        List<Comment> comments = commentRepository.findByFeature(feature.get());
        return commentMapper.toDtoList(comments);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> findRepliesByParentId(Long parentId) {
        Optional<Comment> parentComment = commentRepository.findById(parentId);
        if (parentComment.isEmpty()) {
            return List.of();
        }
        return commentMapper.toDtoList(parentComment.get().getReplies());
    }

    public record CreateCommentCommand(String text, String author, String featureCode, String releaseCode) {}

    public record AddReplyCommand(Long parentId, String text, String author) {}
}