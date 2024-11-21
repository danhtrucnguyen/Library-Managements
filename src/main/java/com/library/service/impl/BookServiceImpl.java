package com.library.service.impl;

import java.io.File;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

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
		Book book = bookRepository.findById(id).orElse(null);
		return book;
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
	
	@Override
	public Boolean deleteBook(Integer id) {
		Book book = bookRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(book)) {
			bookRepository.delete(book);
			return true;
		}
		return false;
	}
	
	@Override
	public Book updateBook(Book book, MultipartFile image) {

		Book dbBook = getBookById(book.getId());

		String imageName = image.isEmpty() ? dbBook.getImage() : image.getOriginalFilename();

		dbBook.setBookName(book.getBookName());
		dbBook.setDescription(book.getDescription());
		dbBook.setAuthor(book.getAuthor());
		dbBook.setCategory(book.getCategory());
		dbBook.setStock(book.getStock());
		dbBook.setImage(imageName);
		dbBook.setIsActive(book.getIsActive());
		dbBook.setIsbn(book.getIsbn());

		

		Book updateProduct = bookRepository.save(dbBook);

		if (!ObjectUtils.isEmpty(updateProduct)) {

			if (!image.isEmpty()) {

				try {
					File saveFile = new ClassPathResource("static/images").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "book_img" + File.separator
							+ image.getOriginalFilename());
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return book;
		}
		return null;
	}
}
