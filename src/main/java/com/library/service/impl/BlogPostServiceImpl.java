package com.library.service.impl;

import com.library.dto.BlogPostDTO;
import com.library.model.Author;
import com.library.model.BlogPost;
import com.library.model.Book;
import com.library.model.User;
import com.library.repository.AuthorRepository;
import com.library.repository.BlogPostRepository;
import com.library.repository.UserRepository;
import com.library.service.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public BlogPost createPost(BlogPost blogPost) {
        blogPost.setCreatedAt(LocalDateTime.now());
        blogPost.setUpdatedAt(LocalDateTime.now());
        return blogPostRepository.save(blogPost);
    }

    @Override
    public BlogPost updatePost(Long id, BlogPost updatedBlogPost) {
        BlogPost existingPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existingPost.setTitle(updatedBlogPost.getTitle());
        existingPost.setPcontent(updatedBlogPost.getPcontent());
        existingPost.setImageUrl(updatedBlogPost.getImageUrl());
        existingPost.setUpdatedAt(LocalDateTime.now());

        return blogPostRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long id) {
        blogPostRepository.deleteById(id);
    }

    @Override
    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll();
    }

    @Override
    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

//    @Override
//    public Page<BlogPost> findPaginated(int page, int size) {
//        Pageable pageable = PageRequest.of(page - 1, size);
//        return blogPostRepository.findAll(pageable);
//    }
    
    @Override
    public Page<BlogPost> findPaginated(Integer pageNo, Integer pageSize) {
    	  Pageable pageable = PageRequest.of(pageNo, pageSize);
    	  return blogPostRepository.findAll(pageable);
    }

    @Override
    public BlogPost createBlogPost(BlogPostDTO blogPostDTO) {
        Integer authorId = blogPostDTO.getAuthorId();
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        BlogPost newBlogPost = BlogPost.builder()
                .title(blogPostDTO.getTitle())
                .pcontent(blogPostDTO.getPcontent())
                .imageUrl(blogPostDTO.getImageUrl())
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();
        return blogPostRepository.save(newBlogPost);
    }
}
