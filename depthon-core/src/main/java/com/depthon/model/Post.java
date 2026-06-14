package com.depthon.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.depthon.domain.Division;
import com.depthon.domain.Subdivision;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private Division division;

    @Enumerated(EnumType.STRING)
    private Subdivision subdivision;

    @Column(columnDefinition = "TEXT")
    private String moderationFeedback;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum PostStatus {
        PENDING, APPROVED, REJECTED, NEEDS_REVISION
    }
}