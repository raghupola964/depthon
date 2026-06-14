package com.depthon.controller;

import com.depthon.dto.CreatePostRequest;
import com.depthon.dto.PostResponse;
import com.depthon.model.Post;
import com.depthon.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.security.Principal;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestBody CreatePostRequest request,
            Principal principal) {

        // The email comes from the VERIFIED TOKEN, not the request body
        String authorEmail = principal.getName();

        Post post = postService.createPost(request, authorEmail);
        return ResponseEntity.ok(Map.of(
                "id", post.getId(),
                "status", post.getStatus().toString(),
                "feedback", post.getModerationFeedback() == null ? "" : post.getModerationFeedback(),
                "message", "The AI Gatekeeper has ruled."
        ));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getFeed() {
        return ResponseEntity.ok(
                postService.getApprovedFeed().stream()
                        .map(PostResponse::from)
                        .toList());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PostResponse>> getMyPosts(@RequestParam String email) {
        return ResponseEntity.ok(
                postService.getMyPosts(email).stream()
                        .map(PostResponse::from)
                        .toList());
    }
}