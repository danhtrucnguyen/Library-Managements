package com.library.repository;

import com.library.model.BlogPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

	Page<BlogPost> findByTitle(String ch, Pageable pageable);
}
