package com.library.controller;

import java.security.Principal;
//import java.util.List;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
//import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;

import com.library.model.Cart;
import com.library.model.Category;
import com.library.model.User;
import com.library.service.CartService;
import com.library.service.CategoryService;
import com.library.service.UserService;

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
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid,HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);
		
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Thêm sách không thành công");
		}else {
			session.setAttribute("succMsg", "Thêm sách thành công");
		}
		return "redirect:/book/" + pid;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {

		User user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
//		if (carts.size() > 0) {
//			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
//			m.addAttribute("totalOrderPrice", totalOrderPrice);
//		}
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

}