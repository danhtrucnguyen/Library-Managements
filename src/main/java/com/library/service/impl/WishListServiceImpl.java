package com.library.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.library.model.Book;
import com.library.model.User;
import com.library.model.WishList;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import com.library.repository.WishListRepository;
import com.library.service.WishListService;

@Service
public class WishListServiceImpl implements WishListService {
	
	@Autowired
	private WishListRepository wishListRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;
	
	@Override
	public WishList saveWishList(Integer bookId, Integer userId, Integer quantity) { // Thêm tham số quantity
	    User userDtls = userRepository.findById(userId).get();
	    Book book = bookRepository.findById(bookId).get();

	    WishList wishListStatus = wishListRepository.findByBookIdAndUserId(bookId, userId);

	    WishList wishlist = null;

	    if (ObjectUtils.isEmpty(wishListStatus)) {
	    	wishlist = new WishList();
	    	wishlist.setBook(book);
	    	wishlist.setUser(userDtls);
	
	    } else {
	    	wishlist = wishListStatus;
	    }

	    WishList saveWishList = wishListRepository.save(wishlist);
	    return saveWishList;
	}
	
	@Override
	public List<WishList> getWishListsByUser(Integer userId) {
		List<WishList> wishLists = wishListRepository.findByUserId(userId);

		List<WishList> updateWishLists = new ArrayList<>();
		for (WishList w : wishLists) {
			updateWishLists.add(w);
		}

		return updateWishLists;
	}

	@Override
	public Integer getCountWishList(Integer userId) {
		Integer countByUserId = wishListRepository.countByUserId(userId);
		return countByUserId;
	}
	
	@Override
	public Boolean deleteWishList(Integer id) {
		WishList wishList = wishListRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(wishList)) {
			wishListRepository.delete(wishList);
			return true;
		}
		return false;
	}

}
