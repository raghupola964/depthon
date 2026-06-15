package com.depthon.controller;

import com.depthon.dto.CreatePostRequest;
import com.depthon.dto.PostResponse;
import com.depthon.model.Post;
import com.depthon.service.PostService;
import com.depthon.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.security.Principal;

import com.depthon.model.User;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final UserService userService;

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
    public ResponseEntity<List<PostResponse>> getMyPosts(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(
                postService.getMyPosts(user).stream()
                        .map(PostResponse::from)
                        .toList());
    }

    @GetMapping("/feed/mine")
    public ResponseEntity<List<PostResponse>> myFeed(Principal principal) {
        String email = principal.getName();              // who's asking (from the token)
        User user = userService.findByEmail(email);      // look them up
        List<Post> posts = postService.getFeedForUser(user);
        return ResponseEntity.ok(
                posts.stream().map(PostResponse::from).toList());
    }
}