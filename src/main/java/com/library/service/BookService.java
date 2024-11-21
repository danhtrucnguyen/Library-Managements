package com.library.service;



import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.library.model.Book;

public interface BookService {
	
	public Book saveBook(Book book);
	
	public List<Book> getAllBooks();
	
	public Boolean deleteBook(Integer id);
	
	public Book getBookById(Integer id);
	
	public Book updateBook(Book product, MultipartFile file);

	public List<Book> getAllActiveBooks(String category);
	
}
