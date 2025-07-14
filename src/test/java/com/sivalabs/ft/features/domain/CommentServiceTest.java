package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import com.sivalabs.ft.features.domain.CommentService.AddReplyCommand;
import com.sivalabs.ft.features.domain.CommentService.CreateCommentCommand;
import com.sivalabs.ft.features.domain.dtos.CommentDto;
import com.sivalabs.ft.features.domain.entities.Comment;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Sql(scripts = {"/test-data.sql"})
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    CommentService commentService;

    @Test
    void createComment() {
        String featureCode = "EVAL-1";
        String releaseCode = "IDEA-2025.2";
        CreateCommentCommand cmd = new CreateCommentCommand("Test comment for feature", "s.v.", featureCode, releaseCode);

        Long commentId = commentService.createComment(cmd);
        assertThat(commentId).isNotNull();
        Optional<Comment> savedComment = commentRepository.findById(commentId);
        assertThat(savedComment).isPresent();
        assertThat(savedComment.get().getText()).isEqualTo("Test comment for feature");
        assertThat(savedComment.get().getAuthor()).isEqualTo("s.v.");
        assertThat(savedComment.get().getFeature()).isNotNull();
        assertThat(savedComment.get().getFeature().getCode()).isEqualTo(featureCode);
        assertThat(savedComment.get().getRelease()).isNotNull();
        assertThat(savedComment.get().getRelease().getCode()).isEqualTo(releaseCode);
    }

    @Test
    void findCommentsByFeature() {
        String featureCode = "EVAL-1";
        String releaseCode = "IDEA-2025.2";
        CreateCommentCommand cmd = new CreateCommentCommand("Test comment for findCommentsByFeature", "s.v.", featureCode, releaseCode);
        commentService.createComment(cmd);
        List<CommentDto> comments = commentService.findCommentsByFeature(featureCode);
        assertThat(comments).isNotEmpty();
        assertThat(comments).anyMatch(comment -> comment.text().equals("Test comment for findCommentsByFeature"));
    }
    
    @Test
    void findCommentsByFeatureWithInvalidFeatureCode() {
        String invalidFeatureCode = "NONEXISTENT";
        List<CommentDto> comments = commentService.findCommentsByFeature(invalidFeatureCode);
        assertThat(comments).isEmpty();
    }
    
    @Test
    void findRepliesByParentId() {
        String featureCode = "EVAL-1";
        String releaseCode = "IDEA-2025.2";
        CreateCommentCommand parentCmd = new CreateCommentCommand("Parent comment", "s.v.", featureCode, releaseCode);
        Long parentId = commentService.createComment(parentCmd);

        Optional<Comment> parentCommentOpt = commentRepository.findById(parentId);
        assertThat(parentCommentOpt).isPresent();
        Comment parentComment = parentCommentOpt.get();
        assertThat(parentComment.getFeature()).isNotNull();
        assertThat(parentComment.getRelease()).isNotNull();
        
        // Create replies and add them to the parent comment
        Comment reply1 = new Comment();
        reply1.setText("Reply 1");
        reply1.setAuthor("e.z.");
        reply1.setCreatedAt(Instant.now());
        reply1.setParentComment(parentComment);
        
        Comment reply2 = new Comment();
        reply2.setText("Reply 2");
        reply2.setAuthor("k.a.");
        reply2.setCreatedAt(Instant.now());
        reply2.setParentComment(parentComment);
        
        parentComment.addReply(reply1);
        parentComment.addReply(reply2);

        commentRepository.save(parentComment);

        List<CommentDto> replies = commentService.findRepliesByParentId(parentId);

        assertThat(replies).hasSize(2);
        assertThat(replies).extracting(CommentDto::text).containsExactlyInAnyOrder("Reply 1", "Reply 2");
    }
    
    @Test
    void findRepliesByParentIdWithInvalidParentId() {
        Long invalidParentId = 999999L;
        List<CommentDto> replies = commentService.findRepliesByParentId(invalidParentId);
        assertThat(replies).isEmpty();
    }
    
    @Test
    void addReplyWithInvalidParentId() {
        // Arrange
        Long invalidParentId = 999999L;
        AddReplyCommand cmd = new AddReplyCommand(invalidParentId, "Reply to non-existent parent", "s.v.");

        assertThatThrownBy(() -> commentService.addReply(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parent comment not found");
    }
    
    @Test
    void addReplyWithValidParentId() {
        final Long commentId = commentService.createComment(new CreateCommentCommand("Test comment", "s.v.", "EVAL-1", "IDEA-2025.2"));
        final Comment testComment = commentRepository.findById(commentId).get();
        assertThat(testComment).isNotNull();

        AddReplyCommand cmd = new AddReplyCommand(commentId.longValue(), "Reply", "e.z.");

        final Long reply = commentService.addReply(cmd);
        final Optional<Comment> replyComment = commentRepository.findById(reply);
        assertThat(replyComment.isPresent()).isTrue();

        assertThat(replyComment.get().getParentComment().getId()).isEqualTo((commentId));
    }
}