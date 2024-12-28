package com.library.controller;

import com.library.dto.BlogPostDTO;
import com.library.model.BlogPost;
import com.library.model.Book;
import com.library.model.Category;
import com.library.model.Publisher;
import com.library.model.User;
import com.library.repository.BlogPostRepository;
import com.library.service.BlogPostService;
import com.library.service.FeedbackService;
import com.library.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class BlogPostController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogPostRepository blogPostRepository;
    
    @Autowired
    private FeedbackService feedbackService;
    
    @ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User userDtls = userService.getUserByEmail(email);
			if (userDtls != null) {
	            m.addAttribute("user", userDtls);
	        } 
		}
		m.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
		
		List<BlogPost> allActivePost = blogPostService.getAllPosts();
		m.addAttribute("posts", allActivePost);
	}

//    @GetMapping("/blog_list")
//    public String listPosts(@RequestParam(defaultValue = "1") int page,
//                            Model model,
//                            @SessionAttribute(name = "user", required = false) User user) {
//        int pageSize = 10;
//        Page<BlogPost> blogPage = blogPostService.findPaginated(page, pageSize);
//
//        model.addAttribute("posts", blogPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", blogPage.getTotalPages());
//
//        if (user != null) {
//            model.addAttribute("user", user);
//        }
//        return "/blog_list";
//    }
    
    
    @GetMapping("/blog_list")
	public String posts(Model m, 
	              
	                    @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
	                    @RequestParam(name = "pageSize", defaultValue = "8") Integer pageSize,
	                    @RequestParam(name = "sortField", defaultValue = "createdDate:desc") String sortParam,
	                    @RequestParam(defaultValue = "") String ch) {

	    // Tách sortField và sortOrder
	    String[] sortParams = sortParam.split(":");
	    String sortField = sortParams[0];
	    String sortOrder = sortParams.length > 1 ? sortParams[1] : "asc";

	    // Gọi service với sortField và sortOrder
//	    Page<Book> page = bookService.getAllActiveBookPagination(pageNo, pageSize, category, publisher, sortField, sortOrder, minPrice, maxPrice);
	    
	    Page<BlogPost> page = null;
		if (ch != null && ch.length() > 0) {
			page = blogPostService.searchBlogPostPagination(pageNo, pageSize, ch);
		} else {
			page = blogPostService.getAllBlogPostPagination(pageNo, pageSize);
		}

	    List<BlogPost> posts = page.getContent();
	    m.addAttribute("posts", posts);
	    m.addAttribute("postsSize", posts.size());
	    m.addAttribute("pageNo", page.getNumber());
	    m.addAttribute("pageSize", pageSize);
	    m.addAttribute("totalElements", page.getTotalElements());
	    m.addAttribute("totalPages", page.getTotalPages());
	    m.addAttribute("isFirst", page.isFirst());
	    m.addAttribute("isLast", page.isLast());
	    m.addAttribute("sortField", sortField);
	   
	    return "blog_list";  // Trả về view sách
	}
    
    @GetMapping("/admin/add_blog_post")
	public String add_blog_post(Model m) {
		return "admin/add_blog_post";
	}

    @PostMapping("/admin/savePost")
	public String saveProduct(@ModelAttribute BlogPost blog, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

		blog.setCreatedAt(LocalDateTime.now());
		DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String formattedDateTime = blog.getCreatedAt().format(formatters);
		blog.setFormattedCreatedAt(formattedDateTime);
		blog.setImage(imageName);
		BlogPost savePost = blogPostService.saveBlogPost(blog);

		if (!ObjectUtils.isEmpty(savePost)) {

			File saveFile = new ClassPathResource("static/images").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "blog_img" + File.separator
					+ image.getOriginalFilename());

			// System.out.println(path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Bài đăng được thêm thành công");
		} else {
			session.setAttribute("errorMsg", "Không thêm được bài đăng");
		}

		return "redirect:/admin/admin_blog_list";
	}

    @GetMapping("/admin/admin_blog_list")
	public String loadViewBlogPost(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		Page<BlogPost> page = null;
		if (ch != null && ch.length() > 0) {
			page = blogPostService.searchBlogPostPagination(pageNo, pageSize, ch);
		} else {
			page = blogPostService.getAllBlogPostPagination(pageNo, pageSize);
		}
		m.addAttribute("blogs", page.getContent());

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		return "admin/admin_blog_list";
	}
    

    @GetMapping("/blog_list/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        BlogPost post = blogPostService.getPostById(id);
        model.addAttribute("post", post);
        return "single_blog_post";
    }
    
    @GetMapping("/admin/deletePost/{id}")
	public String deletePost(@PathVariable Long id, HttpSession session) {
		Boolean deleteBook = blogPostService.deletePost(id);
		if (deleteBook) {
			session.setAttribute("succMsg", "Bài đăng được xóa khỏi hệ thống");
		} else {
			session.setAttribute("errorMsg", "Có lỗi xảy ra");
		}
		return "redirect:/admin/admin_blog_list";
	}
    
    @GetMapping("/admin/editPost/{id}")
	public String editPost(@PathVariable Long id, Model m) {
		m.addAttribute("post", blogPostService.getPostById(id));
		return "admin/edit_blog";
	}

    @PostMapping("/admin/updatePost")
	public String updateBook(@ModelAttribute BlogPost post, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		
			BlogPost updateBook = blogPostService.updatePost(post, image);
			if (!ObjectUtils.isEmpty(updateBook)) {
				session.setAttribute("succMsg", "Bài đăng được cập nhật thành công");
			} else {
				session.setAttribute("errorMsg", "Có lỗi xảy ra");
			}
		
		return "redirect:/admin/editPost/" + post.getId();
	}

}
