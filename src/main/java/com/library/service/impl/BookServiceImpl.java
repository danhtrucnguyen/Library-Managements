package com.library.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.service.BookService;

@Service
public class BookServiceImpl implements BookService {
	
	@Autowired
	private BookRepository bookRepository;

	@Override
	public Book saveBook(Book book) {
		return bookRepository.save(book);
	}
	
	@Override
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}
	
	@Override
	public Book getBookById(Integer id) {
		Book product = bookRepository.findById(id).orElse(null);
		return product;
	}
	
	@Override
	public List<Book> getAllActiveBooks(String category) {
		List<Book> books = null;
		if (ObjectUtils.isEmpty(category)) {
			books = bookRepository.findByIsActiveTrue();
		}else {
			books=bookRepository.findByCategory(category);
		}

		return books;
	}
}
