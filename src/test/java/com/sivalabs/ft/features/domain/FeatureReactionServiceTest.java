package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.TestcontainersConfiguration;
import com.sivalabs.ft.features.domain.FeatureReactionService.AddOrUpdateReactionCommand;
import com.sivalabs.ft.features.domain.FeatureReactionService.RemoveReactionCommand;
import com.sivalabs.ft.features.domain.dtos.FeatureDto;
import com.sivalabs.ft.features.domain.dtos.FeatureReactionDto;
import com.sivalabs.ft.features.domain.entities.FeatureReaction;
import com.sivalabs.ft.features.domain.models.ReactionType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Sql(scripts = {"/test-data.sql"})
@Transactional
class FeatureReactionServiceTest {

    @Autowired
    private FeatureReactionRepository featureReactionRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private FeatureReactionService featureReactionService;

    @Test
    void addReaction() {
        String featureCode = "EVAL-1";
        String userId = "s.v.";
        AddOrUpdateReactionCommand cmd = new AddOrUpdateReactionCommand(featureCode, userId, ReactionType.LIKE);

        Long reactionId = featureReactionService.addOrUpdateReaction(cmd);

        assertThat(reactionId).isNotNull();
        Optional<FeatureReaction> savedReaction = featureReactionRepository.findById(reactionId);
        assertThat(savedReaction).isPresent();
        assertThat(savedReaction.get().getUserId()).isEqualTo(userId);
        assertThat(savedReaction.get().getReactionType()).isEqualTo(ReactionType.LIKE);
        assertThat(savedReaction.get().getFeature().getCode()).isEqualTo(featureCode);
    }

    @Test
    void addReactionWithInvalidFeatureCode() {
        String invalidFeatureCode = "NONEXISTENT";
        String userId = "s.v.";
        AddOrUpdateReactionCommand cmd = new AddOrUpdateReactionCommand(invalidFeatureCode, userId, ReactionType.LIKE);

        assertThatThrownBy(() -> featureReactionService.addOrUpdateReaction(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Feature not found with code: " + invalidFeatureCode);
    }

    @Test
    void updateReactionWithExistingReaction() {
        String featureCode = "EVAL-1";
        String userId = "s.v.";

        AddOrUpdateReactionCommand addCmd = new AddOrUpdateReactionCommand(featureCode, userId, ReactionType.LIKE);
        Long reactionId = featureReactionService.addOrUpdateReaction(addCmd);

        AddOrUpdateReactionCommand updateCmd = new AddOrUpdateReactionCommand(featureCode, userId, ReactionType.DISLIKE);

        Long updatedReactionId = featureReactionService.addOrUpdateReaction(updateCmd);

        assertThat(updatedReactionId).isEqualTo(reactionId); // Same ID as it's an update
        Optional<FeatureReaction> updatedReaction = featureReactionRepository.findById(reactionId);
        assertThat(updatedReaction).isPresent();
        assertThat(updatedReaction.get().getReactionType()).isEqualTo(ReactionType.DISLIKE);
        assertThat(updatedReaction.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void removeReaction() {
        String featureCode = "EVAL-1";
        String userId = "test-user";

        AddOrUpdateReactionCommand addCmd = new AddOrUpdateReactionCommand(featureCode, userId, ReactionType.LIKE);
        Long reactionId = featureReactionService.addOrUpdateReaction(addCmd);

        assertThat(featureReactionRepository.findById(reactionId)).isPresent();

        RemoveReactionCommand removeCmd = new RemoveReactionCommand(featureCode, userId);

        featureReactionService.removeReaction(removeCmd);

        assertThat(featureReactionRepository.findById(reactionId)).isEmpty();
    }


    @Test
    void getReactionsByFeature() {
        String featureCode = "EVAL-1";

        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand(featureCode, "s.v.", ReactionType.LIKE));
        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand(featureCode, "e.z.", ReactionType.DISLIKE));

        List<FeatureReactionDto> reactions = featureReactionService.getReactionsByFeature(featureCode);

        assertThat(reactions).hasSize(2);
        assertThat(reactions).extracting(FeatureReactionDto::userId).containsExactlyInAnyOrder("s.v.", "e.z.");
        assertThat(reactions).extracting(FeatureReactionDto::reactionType).containsExactlyInAnyOrder(ReactionType.LIKE, ReactionType.DISLIKE);
    }

    @Test
    void getUserReactionForFeature_withExistingReaction_shouldReturnReaction() {
        String featureCode = "EVAL-1";
        String userId = "s.v.";

        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand(featureCode, userId, ReactionType.LIKE));

        Optional<FeatureReactionDto> reactionOpt = featureReactionService.getUserReactionForFeature(featureCode, userId);

        assertThat(reactionOpt).isPresent();
        assertThat(reactionOpt.get().userId()).isEqualTo(userId);
        assertThat(reactionOpt.get().reactionType()).isEqualTo(ReactionType.LIKE);
        assertThat(reactionOpt.get().featureCode()).isEqualTo(featureCode);
    }


    @Test
    void getMostLikedFeatures() {
        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand("EVAL-1", "user1", ReactionType.LIKE));
        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand("EVAL-1", "user2", ReactionType.LIKE));
        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand("EVAL-1", "user3", ReactionType.DISLIKE));
        
        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand("IDEA-2", "user1", ReactionType.LIKE));
        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand("IDEA-2", "user2", ReactionType.LIKE));
        featureReactionService.addOrUpdateReaction(new AddOrUpdateReactionCommand("IDEA-2", "user3", ReactionType.LIKE));

        List<FeatureDto> mostLikedFeatures = featureReactionService.getMostLikedFeatures(10);

        assertThat(mostLikedFeatures).isNotEmpty();
        // IDEA-2 should be first as it has a higher like score
        assertThat(mostLikedFeatures.get(0).code()).isEqualTo("IDEA-2");
    }
}