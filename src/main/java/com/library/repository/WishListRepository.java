package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.WishList;

public interface WishListRepository  extends JpaRepository<WishList, Integer> {
	
	public WishList findByBookIdAndUserId(Integer bookId, Integer userId);

	public Integer countByUserId(Integer userId);

	public List<WishList> findByUserId(Integer userId);
	
	void deleteByUserId(Integer userId);

}
