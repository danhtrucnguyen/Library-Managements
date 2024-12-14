package com.library.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.Book;

public interface BookRepository extends JpaRepository<Book,Integer> {
	
	List<Book> findByIsActiveTrue();
	
	Page<Book> findByIsActiveTrue(Pageable pageable);

	List<Book> findByCategory(String category);
	
	List<Book> findByPublisher(String publisher);

	List<Book> findByCategoryAndPublisherAndIsActiveTrue(String category, String publisher);
	
	Page<Book> findByCategoryAndPublisherAndIsActiveTrue(Pageable pageable, String category, String publisher);

	List<Book> findByCategoryAndIsActiveTrue(String category);
	
	Page<Book> findByCategoryAndIsActiveTrue(Pageable pageable, String category);

	List<Book> findByPublisherAndIsActiveTrue(String publisher);
	
	Page<Book> findByPublisherAndIsActiveTrue(Pageable pageable,String publisher);
	
	List<Book> findByBookNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);
	
	Page<Book> findByCategory(Pageable pageable,String category);

	Page<Book> findByBookNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2, Pageable pageable);
}
