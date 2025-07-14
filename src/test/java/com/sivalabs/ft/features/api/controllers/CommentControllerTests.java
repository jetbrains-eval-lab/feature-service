package com.sivalabs.ft.features.api.controllers;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.WithMockOAuth2User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class CommentControllerTests extends AbstractIT {

    @Test
    @WithMockOAuth2User(username = "user")
    void createComment() {
        var payload =
                """
                {
                    "text": "This is a test comment on a feature",
                    "featureCode": "EVAL-1",
                    "releaseCode": "IDEA-2025.2"
                }
                """;

        var result = mvc.post()
                .uri("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
        String location = result.getMvcResult().getResponse().getHeader("Location");
        assertThat(location).isNotNull();
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void createCommentWithoutFeatureOrRelease() {
        var payload =
                """
                {
                    "text": "This is a test comment without feature or release"
                }
                """;

        var result = mvc.post()
                .uri("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void addReplyToComment() {
        // First create a comment
        var commentPayload =
                """
                {
                    "text": "This is a test comment on a feature",
                    "featureCode": "EVAL-1",
                    "releaseCode": "IDEA-2025.2"
                }
                """;

        var commentResult = mvc.post()
                .uri("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentPayload)
                .exchange();
        String location = commentResult.getMvcResult().getResponse().getHeader("Location");
        String commentId = location.substring(location.lastIndexOf("/") + 1);

        // Now add a reply
        var replyPayload =
                """
                {
                    "text": "This is a reply to the parent comment"
                }
                """;

        var replyResult = mvc.post()
                .uri("/api/comments/{id}/replies", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyPayload)
                .exchange();
        assertThat(replyResult).hasStatus(HttpStatus.CREATED);
        String replyLocation = replyResult.getMvcResult().getResponse().getHeader("Location");
        assertThat(replyLocation).isNotNull();
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void addReplyToNonExistentComment() {
        var replyPayload =
                """
                {
                    "text": "This is a reply to a non-existent comment"
                }
                """;

        var replyResult = mvc.post()
                .uri("/api/comments/{id}/replies", "999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyPayload)
                .exchange();
        assertThat(replyResult).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void getCommentsByFeature() {
        var result = mvc.get()
                .uri("/api/comments/feature/{featureCode}", "EVAL-1")
                .exchange();
        assertThat(result).hasStatusOk();
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void getRepliesForComment() {
        // Create a comment
        var commentPayload =
                """
                {
                    "text": "This is a test comment on a feature",
                    "featureCode": "EVAL-1",
                    "releaseCode": "IDEA-2025.2"
                }
                """;

        var commentResult = mvc.post()
                .uri("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentPayload)
                .exchange();
        
        String location = commentResult.getMvcResult().getResponse().getHeader("Location");
        String commentId = location.substring(location.lastIndexOf("/") + 1);

        // Add a reply
        var replyPayload =
                """
                {
                    "text": "This is a reply for testing"
                }
                """;

        mvc.post()
                .uri("/api/comments/{id}/replies", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyPayload)
                .exchange();

        var result = mvc.get()
                .uri("/api/comments/{id}/replies", commentId)
                .exchange();
        assertThat(result).hasStatusOk();
    }
}