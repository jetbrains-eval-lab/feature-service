package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import com.sivalabs.ft.features.domain.entities.Comment;
import com.sivalabs.ft.features.domain.entities.Feature;
import com.sivalabs.ft.features.domain.entities.Product;
import com.sivalabs.ft.features.domain.models.FeatureStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FeatureRepository featureRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    private Feature testFeature;
    
    @BeforeEach
    void setUp() {
        // Create a product
        Product product = new Product();
        product.setCode("EVAL-PROJECT");
        product.setPrefix("TEST");
        product.setName("EVAL-PROJECT: comments and reactions");
        product.setImageUrl("https://example.com/test-image.png");
        product.setCreatedBy("s.v.");
        product.setCreatedAt(Instant.now());
        Product savedProduct = productRepository.save(product);
        
        // Create a feature
        Feature feature = new Feature();
        feature.setCode("EVAL-PROJECT");
        feature.setTitle("EVAL-PROJECT: comments analysis ");
        feature.setStatus(FeatureStatus.NEW);
        feature.setCreatedBy("s.v.");
        feature.setCreatedAt(Instant.now());
        feature.setProduct(savedProduct);
        testFeature = featureRepository.save(feature);
    }

    @Test
    void testCreateComment() {
        Comment comment = new Comment();
        final String testCommentText = "This is a test comment";

        comment.setText(testCommentText);
        comment.setAuthor("s.v.");
        comment.setCreatedAt(Instant.now());
        comment.setFeature(testFeature);

        Comment savedComment = commentRepository.save(comment);

        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo(testCommentText);
        assertThat(savedComment.getAuthor()).isEqualTo("s.v.");
        assertThat(savedComment.getFeature().getId()).isEqualTo(testFeature.getId());
    }
    
    @Test
    void testUpdateComment() {
        final String testCommentText = "This is a test comment for update";
        Comment comment = new Comment();
        comment.setText(testCommentText);
        comment.setAuthor("s.v.");
        comment.setCreatedAt(Instant.now());
        comment.setFeature(testFeature);
        
        Comment savedComment = commentRepository.save(comment);

        final String updatedCommentText = "Updated comment";
        savedComment.setText(updatedCommentText);
        Comment updatedComment = commentRepository.save(savedComment);

        assertThat(updatedComment.getId()).isEqualTo(savedComment.getId());
        assertThat(updatedComment.getText()).isEqualTo(updatedCommentText);
        assertThat(updatedComment.getAuthor()).isEqualTo("s.v.");
    }
    
    @Test
    void testDeleteComment() {
        Comment comment = new Comment();
        comment.setText("Comment to delete");
        comment.setAuthor("s.v.");
        comment.setCreatedAt(Instant.now());
        comment.setFeature(testFeature);
        
        Comment savedComment = commentRepository.save(comment);
        Long commentId = savedComment.getId();

        commentRepository.delete(savedComment);

        Optional<Comment> deletedComment = commentRepository.findById(commentId);
        assertThat(deletedComment).isEmpty();
    }
    
    @Test
    void testFindByFeature() {
        Comment comment1 = new Comment();
        comment1.setText("Comment 1");
        comment1.setAuthor("s.v.");
        comment1.setCreatedAt(Instant.now());
        comment1.setFeature(testFeature);
        
        Comment comment2 = new Comment();
        comment2.setText("Comment 2");
        comment2.setAuthor("e.z.");
        comment2.setCreatedAt(Instant.now());
        comment2.setFeature(testFeature);
        
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByFeature(testFeature);

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText).containsExactlyInAnyOrder("Comment 1", "Comment 2");
    }
    
    @Test
    void testFindByAuthor() {
        Comment comment1 = new Comment();
        final String text = "Comment by S.V.";
        comment1.setText(text);
        comment1.setAuthor("s.v.");
        comment1.setCreatedAt(Instant.now());
        comment1.setFeature(testFeature);
        
        commentRepository.save(comment1);

        List<Comment> comments = commentRepository.findByAuthor("s.v.");

        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getText()).isEqualTo(text);
    }
    
    @Test
    void testCommentHierarchy() {
        // Create parent comment
        Comment parentComment = new Comment();
        parentComment.setText("Parent comment");
        parentComment.setAuthor("s.v.");
        parentComment.setCreatedAt(Instant.now());
        parentComment.setFeature(testFeature);
        Comment savedParentComment = commentRepository.save(parentComment);
        
        // Create child comments
        Comment reply1 = new Comment();
        reply1.setText("Reply 1");
        reply1.setAuthor("e.z.");
        reply1.setCreatedAt(Instant.now());
        reply1.setFeature(testFeature);
        
        Comment reply2 = new Comment();
        reply2.setText("Reply 2");
        reply2.setAuthor("k.a.");
        reply2.setCreatedAt(Instant.now());
        reply2.setFeature(testFeature);
        
        // Save replies first to get IDs
        Comment savedReply1 = commentRepository.save(reply1);
        Comment savedReply2 = commentRepository.save(reply2);
        
        // Add replies to parent using addReply method
        savedParentComment.addReply(savedReply1);
        savedParentComment.addReply(savedReply2);
        
        // Save parent with updated relationships
        commentRepository.save(savedParentComment);
        
        // Refresh all entities from database
        Comment updatedParentComment = commentRepository.findById(savedParentComment.getId()).orElseThrow();
        savedReply1 = commentRepository.findById(savedReply1.getId()).orElseThrow();
        savedReply2 = commentRepository.findById(savedReply2.getId()).orElseThrow();
        
        // Verify parent-child relationships
        assertThat(updatedParentComment.getReplies()).hasSize(2);
        assertThat(updatedParentComment.getReplies()).extracting(Comment::getText).containsExactlyInAnyOrder("Reply 1", "Reply 2");
        
        // Verify child-parent relationships
        assertThat(savedReply1.getParentComment()).isNotNull();
        assertThat(savedReply1.getParentComment().getId()).isEqualTo(savedParentComment.getId());
        
        // Test removing a reply
        updatedParentComment.removeReply(savedReply1);
        commentRepository.save(updatedParentComment);
        
        // Refresh entities again
        Comment finalParentComment = commentRepository.findById(savedParentComment.getId()).orElseThrow();
        Comment detachedReply = commentRepository.findById(savedReply1.getId()).orElseThrow();
        
        // Verify reply was removed from parent
        assertThat(finalParentComment.getReplies()).hasSize(1);
        assertThat(finalParentComment.getReplies()).extracting(Comment::getText).containsExactly("Reply 2");
        
        // Verify parent reference was removed from reply
        assertThat(detachedReply.getParentComment()).isNull();
    }
}