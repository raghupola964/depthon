package com.depthon.repository;

import com.depthon.model.Post;
import com.depthon.model.Report;
import com.depthon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // How many reports does this post have?
    long countByPost(Post post);

    // Has this user already reported this post?
    boolean existsByReporterAndPost(User reporter, Post post);
}