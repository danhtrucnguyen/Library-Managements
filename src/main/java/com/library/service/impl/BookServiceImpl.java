package com.library.service.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
	public List<Book> getAllActiveBooks(String category, String publisher) {
	    List<Book> books = null;

	    if (ObjectUtils.isEmpty(category) && ObjectUtils.isEmpty(publisher)) {
	        // Nếu không có category và publisher, trả về tất cả sách đang hoạt động
	        books = bookRepository.findByIsActiveTrue();
	    } else if (!ObjectUtils.isEmpty(category) && !ObjectUtils.isEmpty(publisher)) {
	        // Nếu có cả category và publisher, lọc theo cả hai
	        books = bookRepository.findByCategoryAndPublisherAndIsActiveTrue(category, publisher);
	    } else if (!ObjectUtils.isEmpty(category)) {
	        // Nếu chỉ có category, lọc theo category
	        books = bookRepository.findByCategoryAndIsActiveTrue(category);
	    } else if (!ObjectUtils.isEmpty(publisher)) {
	        // Nếu chỉ có publisher, lọc theo publisher
	        books = bookRepository.findByPublisherAndIsActiveTrue(publisher);
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
		dbBook.setPublisher(book.getPublisher());
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
	
	@Override
	public List<Book> searchBook(String ch) {
		return bookRepository.findByBookNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
	}

	@Override
	public void saveBooksFromExcel(MultipartFile file) throws Exception {
		List<Book> books = new ArrayList<>();
		try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
			Sheet sheet = workbook.getSheetAt(0);
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Book book = new Book();
					book.setBookName(row.getCell(0).getStringCellValue());
					book.setDescription(row.getCell(1).getStringCellValue());
					book.setAuthor(row.getCell(2).getStringCellValue());
					book.setCategory(row.getCell(3).getStringCellValue());
					book.setPublisher(row.getCell(4).getStringCellValue());
					book.setStock((int) row.getCell(5).getNumericCellValue());
					book.setImage(row.getCell(6).getStringCellValue());
					book.setIsbn(row.getCell(7).getStringCellValue());
					book.setIsActive(row.getCell(8).getBooleanCellValue());
					books.add(book);
				}
			}
		}
		bookRepository.saveAll(books);
	}
}
