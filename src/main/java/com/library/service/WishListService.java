package com.library.service;

import java.util.List;

import com.library.model.WishList;


public interface WishListService {
	
	public WishList saveWishList(Integer bookId, Integer userId, Integer quantity);
	
	public List<WishList> getWishListsByUser(Integer userId);
	
	public Integer getCountWishList(Integer userId);
	
	public Boolean deleteWishList(Integer id);

}
