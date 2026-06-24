package com.depthon.service;

import com.depthon.dto.JudgeVerdict;
import com.depthon.model.Post;
import com.depthon.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Map;
import org.springframework.cache.annotation.CacheEvict;



@Service
@RequiredArgsConstructor
public class JudgingConsumer {

    private final PostRepository postRepository;
    private final GatekeeperClient gatekeeperClient;
    private final SimpMessagingTemplate messagingTemplate;  // for sending WebSocket messages to clients

    @CacheEvict(value = "userFeed", allEntries = true)
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
                        // NEW: push the verdict to the browser over WebSocket
            messagingTemplate.convertAndSend("/topic/verdicts", Map.of(
                    "postId", post.getId(),
                    "status", verdict.getDecision(),
                    "feedback", verdict.getFeedback() == null ? "" : verdict.getFeedback()
            ));
        } catch (Exception e) {
            System.err.println("Gatekeeper unavailable for post " + postId + ": " + e.getMessage());
            // Post stays PENDING; could add retry logic later
        }
    }
}