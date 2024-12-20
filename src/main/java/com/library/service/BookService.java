package com.library.service;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.library.model.Book;

public interface BookService {
	
	public Book saveBook(Book book);
	
	public List<Book> getAllBooks();
	
	public Boolean deleteBook(Integer id);
	
	public Book getBookById(Integer id);
	
	public Book updateBook(Book product, MultipartFile file);

	public List<Book> getAllActiveBooks(String category, String publisher);
	
//	public List<Book> getAllActiveBooksPublisher(String publisher);
	
	public List<Book> searchBook(String ch);

	void saveBooksFromExcel(MultipartFile file) throws Exception;
	
//	public Page<Book> getAllActiveBookPagination(Integer pageNo, Integer pageSize, String category, String publisher);
	
	public Page<Book> searchActiveBookPagination(Integer pageNo, Integer pageSize, String category, String ch);

	public Page<Book> searchBookPagination(Integer pageNo, Integer pageSize, String ch);

	public Page<Book> getAllBooksPagination(Integer pageNo, Integer pageSize);
	
	public Page<Book> getAllActiveBookPagination(Integer pageNo, Integer pageSize, String category, String publisher, 
            String sortField, String sortOrder, Double minPrice, Double maxPrice);

}
