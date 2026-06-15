package com.depthon.controller;

import com.depthon.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // POST /api/posts/{id}/report  - report a post as wrong-field
    @PostMapping("/{id}/report")
    public ResponseEntity<Map<String, String>> reportPost(
            @PathVariable Long id,
            Principal principal) {
        String message = reportService.reportPost(principal.getName(), id);
        return ResponseEntity.ok(Map.of("message", message));
    }
}