package com.library.controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
//import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;

import com.library.model.BookOrder;
import com.library.model.BookOrderItem;
import com.library.model.Cart;
import com.library.model.Category;
import com.library.model.OrderRequest;
import com.library.model.User;
import com.library.service.OrderService;
import com.library.service.CartService;
import com.library.service.CategoryService;
import com.library.service.UserService;
import com.library.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

//import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

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

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid,
			@RequestParam(required = false, defaultValue = "1") Integer quantity, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid, quantity); // Truyền quantity vào service

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Thêm sách không thành công");
		} else {
			session.setAttribute("succMsg", "Thêm sách thành công");
		}
		return "redirect:/book/" + pid;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {

		User user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.isEmpty()) {
			m.addAttribute("emptyCartMsg", "Giỏ hàng của bạn hiện tại trống.");
		} else {
			m.addAttribute("carts", carts);
			Integer totalOrderPrice = (int) carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	private User getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		User userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) { // Principal p, Model m
		User user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Integer orderPrice = (int) carts.get(carts.size() - 1).getTotalOrderPrice();
			Integer totalOrderPrice = (int) carts.get(carts.size() - 1).getTotalOrderPrice() + 20000;
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/order";
	}

	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) {
		// System.out.println(request);
		User user = getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), request);
		cartService.clearCartByUser(user.getId());
		
		
		if ("vnpay".equals(request.getPaymentType())) {
	        // Nếu thanh toán qua VNPay, chuyển tới trang xử lý VNPay
	        return "redirect:/user/vnpay";
	    } else {
	    	
	        // Nếu thanh toán khi nhận hàng, chuyển tới trang thành công
	        return "redirect:/user/success";
	    }
		

	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}

	@GetMapping("/vnpay")
	public String redirectToVNPay(Principal p, Model m) {
		User user = getLoggedInUserDetails(p);

		List<Cart> carts = cartService.getCartsByUser(user.getId());
		List<BookOrder> orders = orderService.getOrdersByUser(user.getId());
		m.addAttribute("orders", orders);
		m.addAttribute("carts", carts);
		if (!orders.isEmpty()) {
			BookOrder latestOrder = orders.get(0); // Lấy đơn hàng cuối cùng
			m.addAttribute("latestOrder", latestOrder); // Truyền đơn hàng mới nhất vào mô hình
		}


		// Logic để chuyển hướng người dùng đến trang thanh toán VNPay
		return "/user/vnpay"; // Trang xử lý VNPay (có thể là redirect đến VNPay hoặc trang thông báo)
	}

	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		User loginUser = getLoggedInUserDetails(p);
		List<BookOrder> orders = orderService.getOrdersByUser(loginUser.getId());

		// Định dạng ngày và giờ theo dd/MM/yyyy HH:mm:ss
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		// Tạo DecimalFormat để định dạng totalAmount
		DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

		for (BookOrder order : orders) {
			// Đổi ngày giờ theo định dạng dd/MM/yyyy HH:mm:ss
			if (order.getOrderDate() != null) {
				String formattedDateTime = order.getOrderDate().format(formatter);
				order.setFormattedOrderDate(formattedDateTime); // Cập nhật ngày giờ đã định dạng
			}

			// Tính tổng giá trị của đơn hàng và định dạng số tiền
			int totalAmount = 20000;
			for (BookOrderItem item : order.getItems()) {
				totalAmount += item.getBook().getDiscountPrice() * item.getQuantity();
			}
			order.setTotalAmount(totalAmount); // Lưu tổng giá trị vào đơn hàng

			// Định dạng totalAmount với dấu phân cách hàng nghìn
			String formattedTotalAmount = decimalFormat.format(totalAmount);
			order.setFormattedTotalAmount(formattedTotalAmount); // Lưu tổng giá trị đã định dạng
		}

		// Thêm danh sách đơn hàng vào model
		m.addAttribute("orders", orders);

		// Trả về trang hiển thị các đơn hàng
		return "/user/my_orders";
	}

	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		Boolean updateOrder = orderService.updateOrderStatus(id, status);

		if (updateOrder) {
			session.setAttribute("succMsg", "Đã cập nhật trạng thái");
		} else {
			session.setAttribute("errorMsg", "Trạng thái chưa được cập nhật");
		}
		return "redirect:/user/user-orders";
	}

}