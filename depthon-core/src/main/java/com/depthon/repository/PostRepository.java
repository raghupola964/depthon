package com.depthon.repository;

import com.depthon.domain.Subdivision;
import com.depthon.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByStatusOrderByCreatedAtDesc(Post.PostStatus status);

    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    List<Post> findByStatusAndSubdivisionOrderByCreatedAtDesc(
            Post.PostStatus status, Subdivision subdivision);
}