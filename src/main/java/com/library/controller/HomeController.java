
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
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.library.util.CommonUtil;

import io.micrometer.common.util.StringUtils;
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
	private CommentService commentService;

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
    private FeedbackService feedbackService;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m,
			@RequestParam(value = "category", defaultValue = "") String category,
			@RequestParam(value = "publisher", defaultValue = "") String publisher) {
		if (p != null) {
			String email = p.getName();
			User userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
		
		List<Publisher> allActivePublisher = publisherService.getAllActivePublisher();
		m.addAttribute("publishers", allActivePublisher);
		
		List<Book> allActivebook = bookService.getAllActiveBooks( category, publisher);
		m.addAttribute("books", allActivebook);

	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@GetMapping("/")
	public String index(Model m) {
		 m.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
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
//	
//	@GetMapping("/books")
//	public String books(Model m, 
//	                    @RequestParam(value = "category", defaultValue = "") String category, 
//	                    @RequestParam(value = "publisher", defaultValue = "") String publisher, 
//	                    @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
//	                    @RequestParam(name = "pageSize", defaultValue = "8") Integer pageSize){
//		
//	    // Lấy danh sách các danh mục
//	    List<Category> categories = categoryService.getAllActiveCategory();
//	    m.addAttribute("paramCategory", category);
//	    m.addAttribute("categories", categories);
//	  
//	    // Lấy danh sách các nhà xuất bản (giả sử bạn có dịch vụ nhà xuất bản)
//	    List<Publisher> publishers = publisherService.getAllActivePublisher(); // Bạn cần tạo dịch vụ này
//	    m.addAttribute("paramPublisher", publisher);
//	    m.addAttribute("publishers", publishers);
//	    
//	    // Lấy sách theo danh mục hoặc nhà xuất bản
////	    List<Book> books = bookService.getAllActiveBooks(category, publisher); // Cập nhật phương thức này để hỗ trợ publisher
//	    
//	    
//	    // Truyền dữ liệu vào mô hình
//	    Page<Book> page = bookService.getAllActiveBookPagination(pageNo, pageSize, category, publisher);
//		List<Book> books = page.getContent();
//	      // Truyền các nhà xuất bản vào
//	    m.addAttribute("books", books);
//	    m.addAttribute("booksSize", books.size());
//
//		m.addAttribute("pageNo", page.getNumber());
//		m.addAttribute("pageSize", pageSize);
//		m.addAttribute("totalElements", page.getTotalElements());
//		m.addAttribute("totalPages", page.getTotalPages());
//		m.addAttribute("isFirst", page.isFirst());
//		m.addAttribute("isLast", page.isLast());
//	   
//	    
//	    return "book";  // Trả về view sách
//	}
	
	@GetMapping("/books")
	public String books(Model m, 
	                    @RequestParam(value = "category", defaultValue = "") String category, 
	                    @RequestParam(value = "publisher", defaultValue = "") String publisher, 
	                    @RequestParam(value = "priceRange", defaultValue = "") String priceRange,
	                    @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
	                    @RequestParam(name = "pageSize", defaultValue = "8") Integer pageSize,
	                    @RequestParam(name = "sortField", defaultValue = "createdDate:desc") String sortParam,
	                    @RequestParam(defaultValue = "") String ch) {
		
		  // Lấy danh sách các danh mục
	    List<Category> categories = categoryService.getAllActiveCategory();
	    m.addAttribute("paramCategory", category);
	    m.addAttribute("categories", categories);
	  
	    // Lấy danh sách các nhà xuất bản (giả sử bạn có dịch vụ nhà xuất bản)
	    List<Publisher> publishers = publisherService.getAllActivePublisher(); // Bạn cần tạo dịch vụ này
	    m.addAttribute("paramPublisher", publisher);
	    m.addAttribute("publishers", publishers);

	    Double minPrice = null;
	    Double maxPrice = null;
	    switch (priceRange) {
	        case "under100":
	            maxPrice = 100000.0;
	            break;
	        case "100to200":
	            minPrice = 100000.0;
	            maxPrice = 200000.0;
	            break;
	        case "200to300":
	            minPrice = 200000.0;
	            maxPrice = 300000.0;
	            break;
	        case "300to400":
	            minPrice = 300000.0;
	            maxPrice = 400000.0;
	            break;
	        case "400to500":
	            minPrice = 400000.0;
	            maxPrice = 500000.0;
	            break;
	        case "above500":
	            minPrice = 500000.0;
	            break;
	    }

	    // Tách sortField và sortOrder
	    String[] sortParams = sortParam.split(":");
	    String sortField = sortParams[0];
	    String sortOrder = sortParams.length > 1 ? sortParams[1] : "asc";

	    // Gọi service với sortField và sortOrder
//	    Page<Book> page = bookService.getAllActiveBookPagination(pageNo, pageSize, category, publisher, sortField, sortOrder, minPrice, maxPrice);
	    
	    Page<Book> page = null;
		if (StringUtils.isEmpty(ch)) {
			page = bookService.getAllActiveBookPagination(pageNo, pageSize, category, publisher, sortField, sortOrder, minPrice, maxPrice);
		} else {
			page = bookService.searchActiveBookPagination(pageNo, pageSize, category, ch);
		}

	    List<Book> books = page.getContent();
	    m.addAttribute("books", books);
	    m.addAttribute("booksSize", books.size());
	    m.addAttribute("pageNo", page.getNumber());
	    m.addAttribute("pageSize", pageSize);
	    m.addAttribute("totalElements", page.getTotalElements());
	    m.addAttribute("totalPages", page.getTotalPages());
	    m.addAttribute("isFirst", page.isFirst());
	    m.addAttribute("isLast", page.isLast());
	    m.addAttribute("sortField", sortField);
	    m.addAttribute("sortOrder", sortOrder);
	    m.addAttribute("priceRange", priceRange);
	   
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
	
	@GetMapping("/category")
	public String category() {
		return "category";
	}
	
	@GetMapping("/publisher")
	public String publisher() {
		return "publisher";
	}

	@GetMapping("/book/{id}")
	public String book(@PathVariable int id, Model m) {
		// Hiển thị danh sách comment
		List<Comment> comments = commentService.getCommentsByBook(id);
		Book bookById = bookService.getBookById(id);
		m.addAttribute("book", bookById);
		m.addAttribute("comments", comments);
		return "view_book";
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
	
//	@GetMapping("/search")
//	public String searchBook(@RequestParam String ch, Model m) {
//		List<Book> searchBooks = bookService.searchBook(ch);
//		if (searchBooks.isEmpty()) {
//	        m.addAttribute("message", "Không tìm thấy sách");
//	    } else {
//	        m.addAttribute("books", searchBooks);
//	    }
//		List<Category> categories = categoryService.getAllActiveCategory();
//		m.addAttribute("categories", categories);
//		List<Publisher> publishers = publisherService.getAllActivePublisher();
//		m.addAttribute("publishers", publishers);
//		return "book";
//
//	} 
//    @GetMapping("/search")
//    public String searchBook(@RequestParam String ch, 
//                             @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
//                             @RequestParam(name = "pageSize", defaultValue = "8") Integer pageSize, 
//                             Model m) {
//    	
//    	
//        // Gọi phương thức service với phân trang
//        Page<Book> searchBooks = bookService.searchBookPagination(pageNo, pageSize, ch);
//        
//        if (searchBooks.isEmpty()) {
//            m.addAttribute("message", "Không tìm thấy sách");
//        } else {
//            m.addAttribute("books", searchBooks.getContent());
//        }
//        
//        // Thêm thông tin phân trang vào model
//        m.addAttribute("pageNo", searchBooks.getNumber());
//        m.addAttribute("pageSize", pageSize);
//        m.addAttribute("totalElements", searchBooks.getTotalElements());
//        m.addAttribute("totalPages", searchBooks.getTotalPages());
//        m.addAttribute("isFirst", searchBooks.isFirst());
//        m.addAttribute("isLast", searchBooks.isLast());
//
//        // Lấy danh mục và nhà xuất bản để hiển thị
//        List<Category> categories = categoryService.getAllActiveCategory();
//        m.addAttribute("categories", categories);
//
//        List<Publisher> publishers = publisherService.getAllActivePublisher();
//        m.addAttribute("publishers", publishers);
//
//        return "book"; // Trả về trang sách
//    }
    


}
