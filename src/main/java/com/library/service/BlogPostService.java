package com.library.service;

import com.library.dto.BlogPostDTO;
import com.library.model.BlogPost;
import com.library.model.Book;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogPostService {
//    BlogPost createPost(BlogPost blogPost);
//
//    BlogPost updatePost(Long id, BlogPost updatedBlogPost);
//
//    void deletePost(Long id);
//
    List<BlogPost> getAllPosts();
//
//    BlogPost getPostById(Long id);
//
//    Page<BlogPost> findPaginated(Integer pageNo, Integer pageSize);
//
//    BlogPost createBlogPost(BlogPostDTO blogPostDTO);
	
	public BlogPost saveBlogPost(BlogPost blog);
	
	public Page<BlogPost> getAllBlogPostPagination(Integer pageNo, Integer pageSize);
	
	public Page<BlogPost> searchBlogPostPagination(Integer pageNo, Integer pageSize, String ch);
	
	public BlogPost getPostById(Long id);
	
	public Boolean deletePost(Long id);
	
	public BlogPost updatePost(BlogPost post, MultipartFile image);
}
