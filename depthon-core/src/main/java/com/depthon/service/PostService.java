package com.depthon.service;

import com.depthon.domain.Subdivision;
import com.depthon.dto.CreatePostRequest;
//import com.depthon.dto.JudgeVerdict;
import com.depthon.dto.PostResponse;
import com.depthon.model.Post;
import com.depthon.model.User;
import com.depthon.repository.PostRepository;
import com.depthon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    //private final GatekeeperClient gatekeeperClient;
    private final JudgingProducer judgingProducer;

    public Post createPost(CreatePostRequest request, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if (request.getContent() == null || request.getContent().length() < 50) {
            throw new RuntimeException("Content must be at least 50 characters - Depthon is for depth");
        }

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);

        // A post inherits its author's field - no cross-posting, no mismatch
        post.setSubdivision(author.getSubdivision());
        post.setDivision(author.getDivision());

        post.setStatus(Post.PostStatus.PENDING);
        Post saved = postRepository.save(post);

        // Send the post to the judging queue (async) instead of judging here.
        // The post stays PENDING; a Kafka consumer will judge it in the background.
        judgingProducer.sendPostForJudging(saved.getId());

        return saved;   // returns immediately, still PENDING - user isn't blocked
    }

    public List<Post> getApprovedFeed() {
        return postRepository.findByStatusOrderByCreatedAtDesc(Post.PostStatus.APPROVED);
    }

    public List<Post> getMyPosts(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public List<Post> getFeedForSubdivision(Subdivision subdivision) {
        return postRepository.findByStatusAndSubdivisionOrderByCreatedAtDesc(
                Post.PostStatus.APPROVED, subdivision);
    }
    @Cacheable(value = "userFeed", key = "#user.id")
    public List<PostResponse> getFeedForUser(User user) {
        java.util.Set<Subdivision> feedSubdivisions = new java.util.HashSet<>();
        feedSubdivisions.add(user.getSubdivision());
        feedSubdivisions.addAll(user.getFollowedSubdivisions());

        List<Post> posts = postRepository.findByStatusAndSubdivisionInAndHiddenFromFeedFalseOrderByCreatedAtDesc(
                Post.PostStatus.APPROVED, feedSubdivisions);

        // Convert to DTOs HERE, so the CACHED value is the serializable DTO list
        return posts.stream().map(PostResponse::from).toList();
    }
}
