
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.library.model.User;
import com.library.model.Book;
import com.library.model.Category;
import com.library.model.Publisher;
import com.library.service.BookService;
import com.library.service.CartService;
import com.library.service.CategoryService;
import com.library.service.PublisherService;
import com.library.service.UserService;
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
			session.setAttribute("succMsg", "Register successfully");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
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
			session.setAttribute("errorMsg", "Invalid email");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			// Generate URL :
			// http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendMail(url, email);

			if (sendMail) {
				session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
			} else {
				session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
			}
		}

		return "redirect:/forgot-password";
	}
	
	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

		User userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			m.addAttribute("msg", "Your link is invalid or expired !!");
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
			m.addAttribute("errorMsg", "Your link is invalid or expired !!");
			return "message";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			// session.setAttribute("succMsg", "Password change successfully");
			m.addAttribute("msg", "Password change successfully");

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
	public String book(@PathVariable int id, Model m) {
		Book bookById = bookService.getBookById(id);
		m.addAttribute("book", bookById);
		return "view_book";
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
