package com.library.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.library.model.Cart;
import com.library.model.Book;
import com.library.model.User;
import com.library.repository.CartRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import com.library.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;

//	@Override
//	public Cart saveCart(Integer bookId, Integer userId) {
//
//		User userDtls = userRepository.findById(userId).get();
//		Book book = bookRepository.findById(bookId).get();
//
//		Cart cartStatus = cartRepository.findByBookIdAndUserId(bookId, userId);
//
//		Cart cart = null;
//
//		if (ObjectUtils.isEmpty(cartStatus)) {
//			cart = new Cart();
//			cart.setBook(book);
//			cart.setUser(userDtls);
//			cart.setQuantity(1);
//			cart.setTotalPrice((int) (1 * book.getDiscountPrice()));
//		} else {
//			cart = cartStatus;
//			cart.setQuantity(cart.getQuantity() + 1);
//			 cart.setTotalPrice((int) (cart.getQuantity() * cart.getBook().getDiscountPrice()));
//		}
//		Cart saveCart = cartRepository.save(cart);
//
//		return saveCart;
//	}
	
//	@Override
//	public Cart saveCart(Integer bookId, Integer userId) {
//	    User userDtls = userRepository.findById(userId).get();
//	    Book book = bookRepository.findById(bookId).get();
//
//	    Cart cartStatus = cartRepository.findByBookIdAndUserId(bookId, userId);
//
//	    Cart cart = null;
//
//	    if (ObjectUtils.isEmpty(cartStatus)) {
//	        cart = new Cart();
//	        cart.setBook(book);
//	        cart.setUser(userDtls);
//	        cart.setQuantity(1);
//	        
//	        // Kiểm tra discountPrice trước khi ép kiểu
//	        Integer discountPrice = book.getDiscountPrice() != null ? book.getDiscountPrice() : 0;
//	        cart.setTotalPrice(discountPrice); // Làm tròn nếu cần thiết
//	    } else {
//	        cart = cartStatus;
//	        cart.setQuantity(cart.getQuantity() + 1);
//	        
//	        Integer discountPrice = book.getDiscountPrice() != null ? book.getDiscountPrice() : 0;
//	        cart.setTotalPrice(cart.getQuantity() * discountPrice);
//	    }
//	    
//	    Cart saveCart = cartRepository.save(cart);
//	    return saveCart;
//	}
	
	@Override
	public Cart saveCart(Integer bookId, Integer userId, Integer quantity) { // Thêm tham số quantity
	    User userDtls = userRepository.findById(userId).get();
	    Book book = bookRepository.findById(bookId).get();

	    Cart cartStatus = cartRepository.findByBookIdAndUserId(bookId, userId);

	    Cart cart = null;

	    if (ObjectUtils.isEmpty(cartStatus)) {
	        cart = new Cart();
	        cart.setBook(book);
	        cart.setUser(userDtls);
	        cart.setQuantity(quantity);  // Sử dụng quantity truyền vào
	        Integer discountPrice = book.getDiscountPrice() != null ? book.getDiscountPrice() : 0;
	        cart.setTotalPrice(discountPrice * quantity);  // Tính toán tổng giá
	    } else {
	        cart = cartStatus;
	        cart.setQuantity(cart.getQuantity() + quantity);  // Cộng thêm số lượng mới vào giỏ
	        Integer discountPrice = book.getDiscountPrice() != null ? book.getDiscountPrice() : 0;
	        cart.setTotalPrice(cart.getQuantity() * discountPrice);  // Tính toán lại tổng giá
	    }

	    Cart saveCart = cartRepository.save(cart);
	    return saveCart;
	}



//	@Override
//	public List<Cart> getCartsByUser(Integer userId) {
//		List<Cart> carts = cartRepository.findByUserId(userId);
//
//		Integer totalOrderPrice = 0;
//		List<Cart> updateCarts = new ArrayList<>();
//		for (Cart c : carts) {
//			Integer totalPrice = (int) (c.getBook().getDiscountPrice() * c.getQuantity());
//			c.setTotalPrice(totalPrice);
//			totalOrderPrice = totalOrderPrice + totalPrice;
//			c.setTotalOrderPrice(totalOrderPrice);
//			updateCarts.add(c);
//		}
//
//		return updateCarts;
//	}
	
	@Override
	public List<Cart> getCartsByUser(Integer userId) {
	    List<Cart> carts = cartRepository.findByUserId(userId);

	    Integer totalOrderPrice = 0;
	    List<Cart> updateCarts = new ArrayList<>();
	    for (Cart c : carts) {
	        Integer discountPrice = c.getBook().getDiscountPrice();
	        
	        // Kiểm tra discountPrice có null không
	        if (discountPrice != null) {
	            Integer totalPrice = discountPrice * c.getQuantity();
	            c.setTotalPrice(totalPrice);
	            totalOrderPrice += totalPrice;
	            c.setTotalOrderPrice(totalOrderPrice);
	        } else {
	            // Xử lý khi discountPrice là null (có thể đặt giá trị mặc định hoặc báo lỗi)
	            c.setTotalPrice(0); // Ví dụ: đặt giá trị mặc định là 0
	            c.setTotalOrderPrice(totalOrderPrice);
	        }
	        
	        updateCarts.add(c);
	    }

	    return updateCarts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		Integer countByUserId = cartRepository.countByUserId(userId);
		return countByUserId;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {

		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;

		if (sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity() - 1;

			if (updateQuantity <= 0) {
				cartRepository.delete(cart);
			} else {
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}

		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}

	}
	
	public void clearCartByUser(Integer userId) {
	    // Tìm tất cả các giỏ hàng của người dùng và xóa chúng
	    List<Cart> carts = cartRepository.findByUserId(userId);
	    for (Cart cart : carts) {
	        cartRepository.delete(cart); // Hoặc tùy theo cách bạn lưu trữ giỏ hàng, có thể là delete từng item
	    }
	}


}