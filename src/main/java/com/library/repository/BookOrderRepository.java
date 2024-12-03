package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.BookOrder;

public interface BookOrderRepository extends JpaRepository<BookOrder, Integer> {

	List<BookOrder> findByUserId(Integer userId);
	
	List<BookOrder> findByUserIdOrderByOrderDateDesc(Integer userId);

}
