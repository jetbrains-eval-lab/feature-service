package com.sivalabs.ft.features.api.controllers;

import com.sivalabs.ft.features.api.models.CreateCommentPayload;
import com.sivalabs.ft.features.api.models.CreateReplyPayload;
import com.sivalabs.ft.features.api.utils.SecurityUtils;
import com.sivalabs.ft.features.domain.CommentService;
import com.sivalabs.ft.features.domain.CommentService.AddReplyCommand;
import com.sivalabs.ft.features.domain.CommentService.CreateCommentCommand;
import com.sivalabs.ft.features.domain.dtos.CommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments API")
class CommentController {
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;

    CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("")
    @Operation(
            summary = "Create a new comment",
            description = "Create a new comment on a feature or release",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Successful response",
                        headers =
                                @Header(
                                        name = "Location",
                                        required = true,
                                        description = "URI of the created comment")),
                @ApiResponse(responseCode = "400", description = "Invalid request"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "403", description = "Forbidden"),
            })
    ResponseEntity<Void> createComment(@RequestBody @Valid CreateCommentPayload payload) {
        // Validate that either featureCode or releaseCode is provided
        if ((payload.featureCode() == null || payload.featureCode().isBlank()) && 
            (payload.releaseCode() == null || payload.releaseCode().isBlank())) {
            return ResponseEntity.badRequest().build();
        }
        
        var username = SecurityUtils.getCurrentUsername();
        var cmd = new CreateCommentCommand(
                payload.text(),
                username,
                payload.featureCode(),
                payload.releaseCode());
        Long id = commentService.createComment(cmd);
        log.info("Created comment with id {}", id);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/{id}/replies")
    @Operation(
            summary = "Add a reply to a comment",
            description = "Add a reply to an existing comment",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Successful response",
                        headers =
                                @Header(
                                        name = "Location",
                                        required = true,
                                        description = "URI of the created reply")),
                @ApiResponse(responseCode = "400", description = "Invalid request"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "403", description = "Forbidden"),
                @ApiResponse(responseCode = "404", description = "Parent comment not found"),
            })
    ResponseEntity<Void> addReply(@PathVariable Long id, @RequestBody @Valid CreateReplyPayload payload) {
        var username = SecurityUtils.getCurrentUsername();
        var cmd = new AddReplyCommand(
                id,
                payload.text(),
                username);
        try {
            Long replyId = commentService.addReply(cmd);
            log.info("Added reply with id {} to comment {}", replyId, id);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .replacePath("/api/comments/{id}")
                    .buildAndExpand(replyId)
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/feature/{featureCode}")
    @Operation(
            summary = "Find comments by feature",
            description = "Find all comments for a feature",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))))
            })
    List<CommentDto> getCommentsByFeature(@PathVariable String featureCode) {
        return commentService.findCommentsByFeature(featureCode);
    }

    @GetMapping("/{id}/replies")
    @Operation(
            summary = "Find replies for a comment",
            description = "Find all replies for a comment",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))))
            })
    List<CommentDto> getReplies(@PathVariable Long id) {
        return commentService.findRepliesByParentId(id);
    }
}