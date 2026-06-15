package com.depthon.service;

import com.depthon.model.Post;
import com.depthon.model.Report;
import com.depthon.model.User;
import com.depthon.repository.PostRepository;
import com.depthon.repository.ReportRepository;
import com.depthon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final long REPORTS_TO_HIDE = 3;

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public String reportPost(String reporterEmail, Long postId) {
        // 1. Find the reporter and the post
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 2. You cannot report your own post
        if (post.getAuthor().getId().equals(reporter.getId())) {
            throw new RuntimeException("You cannot report your own post");
        }

        // 3. ABUSE CHECK: you can only report a post that is in a field YOU follow
        //    (your home subdivision, or one of your followed subdivisions)
        boolean inYourFields =
                post.getSubdivision() == reporter.getSubdivision()
                || reporter.getFollowedSubdivisions().contains(post.getSubdivision());
        if (!inYourFields) {
            throw new RuntimeException(
                "You can only report posts in fields you follow");
        }

        // 4. Have you already reported this post?
        if (reportRepository.existsByReporterAndPost(reporter, post)) {
            throw new RuntimeException("You have already reported this post");
        }

        // 5. Record the report
        Report report = new Report();
        report.setReporter(reporter);
        report.setPost(post);
        reportRepository.save(report);

        // 6. Count distinct reports; hide the post if it hit the threshold
        long count = reportRepository.countByPost(post);
        if (count >= REPORTS_TO_HIDE && !post.isHiddenFromFeed()) {
            post.setHiddenFromFeed(true);
            postRepository.save(post);
            return "Post reported. It has now been hidden from the feed (" + count + " reports).";
        }

        return "Post reported (" + count + " of " + REPORTS_TO_HIDE + " needed to hide).";
    }
}