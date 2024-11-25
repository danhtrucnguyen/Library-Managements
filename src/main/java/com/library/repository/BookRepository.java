package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.Book;

public interface BookRepository extends JpaRepository<Book,Integer> {
	
	List<Book> findByIsActiveTrue();

	List<Book> findByCategory(String category);
	
	List<Book> findByPublisher(String publisher);

	List<Book> findByCategoryAndPublisherAndIsActiveTrue(String category, String publisher);

	List<Book> findByCategoryAndIsActiveTrue(String category);

	List<Book> findByPublisherAndIsActiveTrue(String publisher);
	
	List<Book> findByBookNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);
}
