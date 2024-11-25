package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {

	
	public Cart findByBookIdAndUserId(Integer bookId, Integer userId);

	public Integer countByUserId(Integer userId);

	public List<Cart> findByUserId(Integer userId);

}
