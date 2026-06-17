package com.depthon.service;

import com.depthon.dto.JudgeVerdict;
import com.depthon.model.Post;
import com.depthon.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JudgingConsumer {

    private final PostRepository postRepository;
    private final GatekeeperClient gatekeeperClient;

    @KafkaListener(topics = "posts-to-judge", groupId = "depthon-judging-group")
    public void judgePost(String postIdString) {
        Long postId = Long.valueOf(postIdString);
        System.out.println("Received post " + postId + " from the judging queue");

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            System.err.println("Post " + postId + " not found, skipping");
            return;
        }

        try {
            JudgeVerdict verdict = gatekeeperClient.judge(
                    post.getId(), post.getTitle(), post.getContent());
            post.setStatus(Post.PostStatus.valueOf(verdict.getDecision()));
            post.setModerationFeedback(verdict.getFeedback());
            postRepository.save(post);
            System.out.println("Post " + postId + " judged: " + verdict.getDecision());
        } catch (Exception e) {
            System.err.println("Gatekeeper unavailable for post " + postId + ": " + e.getMessage());
            // Post stays PENDING; could add retry logic later
        }
    }
}