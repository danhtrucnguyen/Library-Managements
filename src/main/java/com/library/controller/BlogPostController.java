package com.library.controller;

import com.library.model.BlogPost;
import com.library.model.User;
import com.library.repository.BlogPostRepository;
import com.library.service.BlogPostService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BlogPostController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";


    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @GetMapping("/blog_list")
    public String listPosts(@RequestParam(defaultValue = "1") int page,
                            Model model,
                            @SessionAttribute(name = "user", required = false) User user) {
        int pageSize = 10;
        Page<BlogPost> blogPage = blogPostService.findPaginated(page, pageSize);

        model.addAttribute("posts", blogPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPage.getTotalPages());

        if (user != null) {
            model.addAttribute("user", user);
        }
        return "/blog_list";
    }

    @GetMapping("/blog_list/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        BlogPost post = blogPostService.getPostById(id);
        model.addAttribute("post", post);
        return "blog/view";
    }

    @GetMapping("/create")
    public String createPostForm(Model model) {
        model.addAttribute("blogPost", new BlogPost());
        return "blog/create";
    }

//    @PostMapping("/create")
//    public String createPost(@ModelAttribute BlogPost blogPost) {
//        blogPostService.createPost(blogPost);
//        return "redirect:/blog-posts";
//    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        BlogPost post = blogPostService.getPostById(id);
        model.addAttribute("blogPost", post);
        return "blog/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute BlogPost blogPost) {
        blogPostService.updatePost(id, blogPost);
        return "redirect:/blog-posts";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        blogPostService.deletePost(id);
        return "redirect:/blog-posts";
    }

    // Hiển thị form tạo mới bài viết
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new BlogPost());
        return "blog-form"; // Tên file HTML form tạo bài viết
    }

    // Lưu bài viết mới
    @PostMapping
    public String saveBlogPost(@ModelAttribute("post") BlogPost post,
                               @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            // Xử lý ảnh tải lên nếu có
            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                imageFile.transferTo(new File(UPLOAD_DIR + fileName));

                // Lưu đường dẫn ảnh vào bài viết
                post.setImageUrl("/uploads/" + fileName);
            }

            // Lưu bài viết vào database
            blogPostRepository.save(post);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/blogs";
    }

    // API upload ảnh cho CKEditor
    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                file.transferTo(new File(UPLOAD_DIR + fileName));

                Map<String, Object> response = new HashMap<>();
                response.put("uploaded", 1);
                response.put("fileName", fileName);
                response.put("url", "/uploads/" + fileName);

                return ResponseEntity.ok(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

}
