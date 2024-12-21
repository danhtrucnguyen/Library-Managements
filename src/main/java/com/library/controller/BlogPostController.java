package com.library.controller;

import com.library.dto.BlogPostDTO;
import com.library.model.BlogPost;
import com.library.model.User;
import com.library.repository.BlogPostRepository;
import com.library.service.BlogPostService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public String listPosts(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                            Model model,
                            @SessionAttribute(name = "user", required = false) User user) {
        // Lấy dữ liệu phân trang từ service
        Page<BlogPost> blogPage = blogPostService.findPaginated(pageNo, pageSize);

        // Thêm dữ liệu bài viết vào model
        model.addAttribute("posts", blogPage.getContent());

        // Thêm thông tin phân trang vào model
        model.addAttribute("pageNo", blogPage.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", blogPage.getTotalElements());
        model.addAttribute("totalPages", blogPage.getTotalPages());
        model.addAttribute("isFirst", blogPage.isFirst());
        model.addAttribute("isLast", blogPage.isLast());
  

        // Kiểm tra nếu có người dùng đăng nhập, thêm thông tin vào model
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "/blog_list";
    }


    @GetMapping("/admin/admin_blog_list")
    public String adminBlogList(Model model) {
        model.addAttribute("posts", blogPostRepository.findAll());
        return "/admin/admin_blog_list";
    }

    @GetMapping("/blog_list/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        BlogPost post = blogPostService.getPostById(id);
        model.addAttribute("post", post);
        return "single_blog_post";
    }

//    @GetMapping("/create")
//    public String createPostForm(Model model) {
//        model.addAttribute("blogPost", new BlogPost());
//        return "blog/create";
//    }

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
    @GetMapping("/admin/add_blog_post")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new BlogPost());
        return "admin/add_blog_post";
    }

    // Tao bài viết mới
    @PostMapping("/admin/add_blog_post")
    public ResponseEntity<?> saveBlogPost(@RequestBody BlogPostDTO blogPostDTO) {
        blogPostDTO.setAuthorId(3);
        BlogPost blogPost =  blogPostService.createBlogPost(blogPostDTO);
        return ResponseEntity.ok(blogPost);
    }

    // API upload ảnh cho CKEditor

//    @PostMapping("/upload-image")
//    @ResponseBody
//    public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile file) {
//        try {
//            if (!file.isEmpty()) {
//                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//                file.transferTo(new File(UPLOAD_DIR + fileName));
//
//                Map<String, Object> response = new HashMap<>();
//                response.put("uploaded", 1);
//                response.put("fileName", fileName);
//                response.put("url", "/uploads/" + fileName);
//
//                return ResponseEntity.ok(response);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.badRequest().build();
//    }
    @PostMapping (value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file
    ){
        try{
            if (file.getSize() == 0 || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Tệp tin trống! Vui lòng tải lên tệp tin hợp lệ.");
            }
            //Kiểm tra kích thước file và định dạng file
            if (file.getSize() > 10 * 1024 * 1024) {// > 10MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).
                        body("File is too large! Maximum size is 10MB");
            }
            // Kiểm tra định dạng file
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("File must be an image");
            }
            //Lưu file
            String filename = storeFile(file);
            return ResponseEntity.ok("lưu ảnh thành công");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Lưu file
    private String storeFile(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        //Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        Path uploadDir = Paths.get("uploadsBlog");
        //Kiểm tra và tạo thư mục nếu không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        //Đường dẫn đầy đủ đến file
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        //Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

}
