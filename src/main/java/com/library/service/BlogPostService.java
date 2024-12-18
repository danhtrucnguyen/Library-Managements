package com.library.service;

import com.library.model.BlogPost;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BlogPostService {
    BlogPost createPost(BlogPost blogPost);

    BlogPost updatePost(Long id, BlogPost updatedBlogPost);

    void deletePost(Long id);

    List<BlogPost> getAllPosts();

    BlogPost getPostById(Long id);

    Page<BlogPost> findPaginated(int page, int size);
}
