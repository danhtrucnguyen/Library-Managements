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
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	public List<BlogPost> getAllPosts() {
		return blogPostRepository.findAll(Sort.by(Sort.Order.desc("id")));
	}

    @Override
	public BlogPost saveBlogPost(BlogPost blog) {
		return blogPostRepository.save(blog);
	}

    @Override
	public Page<BlogPost> getAllBlogPostPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
		return blogPostRepository.findAll(pageable);
	}
    
    @Override
	public Page<BlogPost> searchBlogPostPagination(Integer pageNo, Integer pageSize, String ch) {
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
		return blogPostRepository.findByTitle(ch, pageable);
	}
    
    @Override
	public BlogPost getPostById(Long id) {
		BlogPost blogPost = blogPostRepository.findById(id).orElse(null);
		return blogPost;
	}
    @Override
    public Boolean deletePost(Long id) {
		BlogPost blogPost = blogPostRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(blogPost)) {
			blogPostRepository.delete(blogPost);
			return true;
		}
		return false;
	}
    
    @Override
	public BlogPost updatePost(BlogPost post, MultipartFile image) {

		BlogPost dbPost = getPostById(post.getId());

		String imageName = image.isEmpty() ? dbPost.getImage() : image.getOriginalFilename();
		DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String formattedDateTime = dbPost.getCreatedAt().format(formatters);
		dbPost.setFormattedCreatedAt(formattedDateTime);
		dbPost.setTitle(post.getTitle());
		dbPost.setContents(post.getContents());
		dbPost.setAuthor(post.getAuthor());
		dbPost.setImage(imageName);
		

		BlogPost updatePost = blogPostRepository.save(dbPost);

		if (!ObjectUtils.isEmpty(updatePost)) {
			if (!image.isEmpty()) {
				try {
					File saveFile = new ClassPathResource("static/images").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "blog_img" + File.separator
							+ image.getOriginalFilename());
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return post;
		}
		return null;
	}
}
