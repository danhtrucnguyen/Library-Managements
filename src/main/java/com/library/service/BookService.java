package com.library.service;



import java.util.List;

import com.library.model.Book;

public interface BookService {
	
	public Book saveBook(Book book);
	
	public List<Book> getAllBooks();
	
	public Book getBookById(Integer id);

	public List<Book> getAllActiveBooks(String category);
	
}
