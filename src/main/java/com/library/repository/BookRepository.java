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
	
	Page<Book> findByCategoryAndPublisherAndIsActiveTrue(String category, String publisher, Pageable pageable);

	Page<Book> findByDiscountPriceGreaterThanEqualAndCategoryAndPublisherAndIsActiveTrue(Double minPrice, String category, String publisher, Pageable pageable);

	Page<Book> findByDiscountPriceLessThanEqualAndCategoryAndPublisherAndIsActiveTrue(Double maxPrice, String category, String publisher, Pageable pageable);

	Page<Book> findByCategoryAndIsActiveTrue(String category, Pageable pageable);

	Page<Book> findByDiscountPriceGreaterThanEqualAndCategoryAndIsActiveTrue(Double minPrice, String category, Pageable pageable);

	Page<Book> findByDiscountPriceLessThanEqualAndCategoryAndIsActiveTrue(Double maxPrice, String category, Pageable pageable);

	Page<Book> findByPublisherAndIsActiveTrue(String publisher, Pageable pageable);

	Page<Book> findByDiscountPriceGreaterThanEqualAndPublisherAndIsActiveTrue(Double minPrice, String publisher, Pageable pageable);

	Page<Book> findByDiscountPriceLessThanEqualAndPublisherAndIsActiveTrue(Double maxPrice, String publisher, Pageable pageable);

	Page<Book> findByDiscountPriceBetweenAndIsActiveTrue(Double minPrice, Double maxPrice, Pageable pageable);

	Page<Book> findByDiscountPriceBetweenAndCategoryAndIsActiveTrue(Double minPrice, Double maxPrice, String category, Pageable pageable);

	Page<Book> findByDiscountPriceBetweenAndPublisherAndIsActiveTrue(Double minPrice, Double maxPrice, String publisher, Pageable pageable);

	Page<Book> findByDiscountPriceGreaterThanEqualAndIsActiveTrue(Double minPrice, Pageable pageable);
	
	Page<Book> findByDiscountPriceLessThanEqualAndIsActiveTrue(Double maxPrice, Pageable pageable);
	
	Page<Book> findByDiscountPriceBetweenAndCategoryAndPublisherAndIsActiveTrue(Double minPrice, Double maxPrice, String category, String publisher, Pageable pageable);

	Page<Book> findByIsActiveTrueAndBookNameContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndPublisherContainingIgnoreCase(
			String ch, String ch2, String ch3, Pageable pageable);
	
	Page<Book> findByisActiveTrueAndBookNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2,
			Pageable pageable);

	
	
}
