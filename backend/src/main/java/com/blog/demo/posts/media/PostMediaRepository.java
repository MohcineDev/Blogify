package com.blog.demo.posts.media;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {

    List<PostMedia> findByPostId(Long postId);

    int countByPostId(Long postId);

    void deleteByFileName(String name);

}
