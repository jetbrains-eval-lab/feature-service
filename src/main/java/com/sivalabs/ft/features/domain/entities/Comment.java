package com.sivalabs.ft.features.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_id_gen")
    @SequenceGenerator(name = "comments_id_gen", sequenceName = "comment_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "text", length = Integer.MAX_VALUE, nullable = false)
    @NotNull
    private String text;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Size(max = 255)
    @NotNull
    @Column(name = "author", nullable = false)
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id")
    private Feature feature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id")
    private Release release;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    public Comment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public void addReply(Comment reply) {
        replies.add(reply);
        reply.setParentComment(this);
    }

    public void removeReply(Comment reply) {
        replies.remove(reply);
        reply.setParentComment(null);
    }
}