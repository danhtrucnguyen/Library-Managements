
package com.library.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
//import java.util.List;
//import java.util.Random;
import java.util.UUID;

import com.library.model.*;
import com.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;


import com.library.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {


	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private PublisherService publisherService;
	
	@Autowired
	private RatingService ratingService;

	@Autowired
	private CommentService commentService;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);

	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@GetMapping("/")
	public String index() {
		return "index";
	}

	

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		User saveUser = userService.saveUser(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());

//				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Đăng ký thành công");
		} else {
			session.setAttribute("errorMsg", "Lỗi máy chủ");
		}

		return "redirect:/register";
	}

//	Forgot Password Code 

	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		User userByEmail = userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Email không hợp lệ");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			// Generate URL :
			// http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendMail(url, email);

			if (sendMail) {
				session.setAttribute("succMsg", "Vui lòng kiểm tra email của bạn..Đã gửi liên kết đặt lại mật khẩu");
			} else {
				session.setAttribute("errorMsg", "Đã xảy ra lỗi trên máy chủ! Email không gửi được");
			}
		}

		return "redirect:/forgot-password";
	}
	
	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

		User userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			m.addAttribute("msg", "Liên kết của bạn không hợp lệ hoặc đã hết hạn !!");
			return "message";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
			Model m) {

		User userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			m.addAttribute("errorMsg", "Liên kết của bạn không hợp lệ hoặc đã hết hạn !!");
			return "message";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			// session.setAttribute("succMsg", "Password change successfully");
			m.addAttribute("msg", "Đổi mật khẩu thành công");

			return "message";
		}

	}
	
	@GetMapping("/books")
	public String books(Model m, 
	                    @RequestParam(value = "category", defaultValue = "") String category, 
	                    @RequestParam(value = "publisher", defaultValue = "") String publisher) {
	    // Lấy danh sách các danh mục
	    List<Category> categories = categoryService.getAllActiveCategory();
	    
	    // Lấy danh sách các nhà xuất bản (giả sử bạn có dịch vụ nhà xuất bản)
	    List<Publisher> publishers = publisherService.getAllActivePublisher(); // Bạn cần tạo dịch vụ này
	    
	    // Lấy sách theo danh mục hoặc nhà xuất bản
	    List<Book> books = bookService.getAllActiveBooks(category, publisher); // Cập nhật phương thức này để hỗ trợ publisher

	    
	    // Truyền dữ liệu vào mô hình
	    m.addAttribute("categories", categories);
	    m.addAttribute("publishers", publishers);  // Truyền các nhà xuất bản vào
	    m.addAttribute("books", books);
	    m.addAttribute("paramCategory", category);  // Truyền giá trị category
	    m.addAttribute("paramPublisher", publisher); // Truyền giá trị publisher
	    
	    return "book";  // Trả về view sách
	}

	
//	@GetMapping("/books")
//	public String books(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
//		// System.out.println("category="+category);
//		List<Category> categories = categoryService.getAllActiveCategory();
//		List<Book> books = bookService.getAllActiveBooks(category);
//		m.addAttribute("categories", categories);
//		m.addAttribute("books", books);
//		m.addAttribute("paramValue", category);
//		return "book";
//	}

	@GetMapping("/book/{id}")
	public String book(@PathVariable int id, Model m, @SessionAttribute(name = "user", required = false) User user) {
		Book bookById = bookService.getBookById(id);
		List<Comment> comments = commentService.getCommentsByBook(id);
		m.addAttribute("book", bookById);
		m.addAttribute("comments", comments);

		if (user != null) {
			m.addAttribute("user", user);
		}
		return "view_book";
	}

	@PostMapping("/book/{id}")
	public String addComment(@PathVariable("id") int bookId,
							 @RequestParam String content,
							 @SessionAttribute(name = "user", required = false) User user,
							 HttpSession session) {
		if (user == null) {
			session.setAttribute("errorMsg", "Bạn phải đăng nhập để bình luận");
			return "redirect:/book/" + bookId;
		}
		commentService.addComment(bookId, user.getId(), content, null);
		return "redirect:/book/" + bookId;
	}

	@PostMapping("/book/{id}/review")
    public String redirectToBookReview(@PathVariable("id") Integer id) {
        return "redirect:/review/" + id;
    }

	
    @GetMapping("/review/{id}")
    public String viewProductReviewPage(@PathVariable("id") Integer id, Model model) {
        Book book = bookService.getBookById(id);
        double averageRating = ratingService.getAverageRating(id);
        List<Rating> ratings = ratingService.getRatingsForBook(id);
        if (book != null ) {
        	model.addAttribute("averageRating", averageRating);
            model.addAttribute("ratings", ratings);
            model.addAttribute("book", book);
          
            return "/user/book-review";
        }
        return "book-not-found";  // Nếu không tìm thấy sách
    }
    
	
    @PostMapping("/saveReview")
    public String addRating( @RequestParam Integer bookId, 
                             @RequestParam Integer userId, 
                             @RequestParam int score, 
                             @RequestParam String review, 
                             Model model) {
 
        // Thêm đánh giá vào cơ sở dữ liệu
        Rating savedRating = ratingService.addRating(bookId, userId, score, review);

        // Cập nhật lại thông tin sách và các đánh giá
        double averageRating = ratingService.getAverageRating(bookId);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("ratings", ratingService.getRatingsForBook(bookId));
        model.addAttribute("bookId", bookId);
        model.addAttribute("userId", userId);
        
        // Chuyển đến trang chi tiết sách, hiển thị đánh giá mới
        return "redirect:/review/" + bookId;  // Tên của template Thymeleaf
    
    }
    
    private User getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		User userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	
	@GetMapping("/search")
	public String searchBook(@RequestParam String ch, Model m) {
		List<Book> searchBooks = bookService.searchBook(ch);
		if (searchBooks.isEmpty()) {
	        m.addAttribute("message", "Không tìm thấy sách");
	    } else {
	        m.addAttribute("books", searchBooks);
	    }
		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("categories", categories);
		return "book";

	}

}
