package com.depthon.service;

import com.depthon.dto.JudgeVerdict;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class GatekeeperClient {

    private final RestClient restClient;

    public GatekeeperClient(@Value("${gatekeeper.url:http://localhost:8000}") String gatekeeperUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(gatekeeperUrl)
                .build();
    }

    public JudgeVerdict judge(Long postId, String title, String content) {
        return restClient.post()
                .uri("/judge")
                .body(Map.of(
                        "post_id", postId,
                        "title", title,
                        "content", content
                ))
                .retrieve()
                .body(JudgeVerdict.class);
    }
}