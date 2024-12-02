package com.library.service;

import java.util.List;

import com.library.model.Cart;

public interface CartService {

	public Cart saveCart(Integer bookId, Integer userId, Integer quantity);

	public List<Cart> getCartsByUser(Integer userId);
	
	public Integer getCountCart(Integer userId);

	public void updateQuantity(String sy, Integer cid);
	
	public void clearCartByUser(Integer userId);

}
