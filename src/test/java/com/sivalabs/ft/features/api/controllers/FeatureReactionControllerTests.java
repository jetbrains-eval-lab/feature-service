package com.sivalabs.ft.features.api.controllers;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.WithMockOAuth2User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureReactionControllerTests extends AbstractIT {

    @Test
    @WithMockOAuth2User(username = "user")
    void addReaction() {
        var payload =
                """
                {
                    "featureCode": "EVAL-1",
                    "reactionType": "LIKE"
                }
                """;

        var result = mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
        String location = result.getMvcResult().getResponse().getHeader("Location");
        assertThat(location).isNotNull();
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void addReactionWithInvalidFeatureCode() {
        var payload =
                """
                {
                    "featureCode": "NONEXISTENT",
                    "reactionType": "LIKE"
                }
                """;

        var result = mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void updateReaction() {
        var addPayload =
                """
                {
                    "featureCode": "EVAL-1",
                    "reactionType": "LIKE"
                }
                """;

        mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPayload)
                .exchange();

        var getUserReactionResult = mvc.get()
                .uri("/api/feature-reactions/feature/{featureCode}/user", "EVAL-1")
                .exchange();
        assertThat(getUserReactionResult)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.reactionType")
                .asString()
                .isEqualTo("LIKE");

        // Then update it to DISLIKE
        var updatePayload =
                """
                {
                    "featureCode": "EVAL-1",
                    "reactionType": "DISLIKE"
                }
                """;

        var updateResult = mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload)
                .exchange();
        assertThat(updateResult).hasStatus(HttpStatus.CREATED);

        getUserReactionResult = mvc.get()
                .uri("/api/feature-reactions/feature/{featureCode}/user", "EVAL-1")
                .exchange();
        assertThat(getUserReactionResult)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.reactionType")
                .asString()
                .isEqualTo("DISLIKE");
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void removeReaction() {
        var addPayload =
                """
                {
                    "featureCode": "EVAL-1",
                    "reactionType": "LIKE"
                }
                """;

        mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPayload)
                .exchange();
        var getUserReactionResult = mvc.get()
                .uri("/api/feature-reactions/feature/{featureCode}/user", "EVAL-1")
                .exchange();
        assertThat(getUserReactionResult)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.reactionType")
                .asString()
                .isEqualTo("LIKE");

        var removeResult = mvc.delete()
                .uri("/api/feature-reactions/{featureCode}", "EVAL-1")
                .exchange();
        assertThat(removeResult).hasStatusOk();

        // Verify the reaction was removed
        getUserReactionResult = mvc.get()
                .uri("/api/feature-reactions/feature/{featureCode}/user", "EVAL-1")
                .exchange();
        assertThat(getUserReactionResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void getReactionsByFeature() {
        var addPayload1 =
                """
                {
                    "featureCode": "EVAL-1",
                    "reactionType": "LIKE"
                }
                """;

        mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPayload1)
                .exchange();

        // Get reactions for the feature
        var getReactionsResult = mvc.get()
                .uri("/api/feature-reactions/feature/{featureCode}", "EVAL-1")
                .exchange();
        assertThat(getReactionsResult).hasStatusOk();
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void getUserReactionForFeature() {
        var addPayload =
                """
                {
                    "featureCode": "EVAL-1",
                    "reactionType": "LIKE"
                }
                """;

        mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPayload)
                .exchange();

        var getUserReactionResult = mvc.get()
                .uri("/api/feature-reactions/feature/{featureCode}/user", "EVAL-1")
                .exchange();
        assertThat(getUserReactionResult)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.reactionType")
                .asString()
                .isEqualTo("LIKE");
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void getMostLikedFeatures() {
        var addPayload1 =
                """
                {
                    "featureCode": "EVAL-1",
                    "reactionType": "LIKE"
                }
                """;

        mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPayload1)
                .exchange();

        var addPayload2 =
                """
                {
                    "featureCode": "IDEA-2",
                    "reactionType": "LIKE"
                }
                """;

        mvc.post()
                .uri("/api/feature-reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addPayload2)
                .exchange();

        // Get most liked features
        var getMostLikedResult = mvc.get()
                .uri("/api/feature-reactions/most-liked")
                .exchange();
        assertThat(getMostLikedResult).hasStatusOk();
    }
}