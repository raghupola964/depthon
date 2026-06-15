package com.depthon.dto;

import com.depthon.model.Post;
import lombok.Data;
import java.time.LocalDateTime;

import com.depthon.domain.Subdivision;

@Data
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private String status;
    private String moderationFeedback;
    private LocalDateTime createdAt;

    private Subdivision subdivision;

    public static PostResponse from(Post post) {
        PostResponse r = new PostResponse();
        r.setId(post.getId());
        r.setTitle(post.getTitle());
        r.setContent(post.getContent());
        r.setAuthorUsername(post.getAuthor().getUsername());
        r.setStatus(post.getStatus().toString());
        r.setModerationFeedback(post.getModerationFeedback());
        r.setCreatedAt(post.getCreatedAt());
        r.setSubdivision(post.getSubdivision());
        return r;
    }
}