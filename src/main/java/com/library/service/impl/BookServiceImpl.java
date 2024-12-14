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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
		dbBook.setPrice(book.getPrice());
		dbBook.setStock(book.getStock());
		dbBook.setImage(imageName);
		dbBook.setIsActive(book.getIsActive());
		dbBook.setIsbn(book.getIsbn());
		dbBook.setDiscount(book.getDiscount());
		Integer discount = (int)(book.getPrice() * (book.getDiscount() / 100.0));
		Integer discountPrice = book.getPrice() - discount;
		dbBook.setDiscountPrice(discountPrice);

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
		try (InputStream inputStream = file.getInputStream();
			 Workbook workbook = new XSSFWorkbook(inputStream)) {
			Sheet sheet = workbook.getSheetAt(0);
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Book book = new Book();
					Cell cell = row.getCell(1); // Tên sách
					if (cell != null) {
						book.setBookName(cell.getStringCellValue());
					}

					cell = row.getCell(2); // Miêu tả sách
					if (cell != null) {
						book.setDescription(cell.getStringCellValue());
					}

					cell = row.getCell(3); // Tác giả
					if (cell != null) {
						book.setAuthor(cell.getStringCellValue());
					}

					cell = row.getCell(4); // Danh mục
					if (cell != null) {
						book.setCategory(cell.getStringCellValue());
					}

					cell = row.getCell(5); // Nhà xuất bản
					if (cell != null) {
						book.setPublisher(cell.getStringCellValue());
					}

					cell = row.getCell(6); // Giá niêm yết
					if (cell != null && cell.getCellType() == CellType.NUMERIC) {
						book.setPrice((int) cell.getNumericCellValue());
					}

					cell = row.getCell(7); // Số lượng
					if (cell != null && cell.getCellType() == CellType.NUMERIC) {
						book.setStock((int) cell.getNumericCellValue());
					}

					cell = row.getCell(8); // Ảnh
					if (cell != null) {
						book.setImage(cell.getStringCellValue());
					}

					cell = row.getCell(9); // Giảm giá
					if (cell != null && cell.getCellType() == CellType.NUMERIC) {
						book.setDiscount((int) cell.getNumericCellValue());
					}

					cell = row.getCell(10); // ISBN
					if (cell != null) {
						book.setIsbn(cell.getStringCellValue());
					}
					book.setIsActive(true);
					book.calculateDiscountPrice();
					books.add(book);
				}
			}
		}
		bookRepository.saveAll(books);
	}
	
	@Override
	public Page<Book> getAllBooksPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize,Sort.by(Sort.Order.desc("createdDate")));
		return bookRepository.findAll(pageable);
	}
	

	@Override
	public Page<Book> searchBookPagination(Integer pageNo, Integer pageSize, String ch) {
		Pageable pageable = PageRequest.of(pageNo, pageSize,Sort.by(Sort.Order.desc("createdDate")));
		return bookRepository.findByBookNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
	}

	@Override
	public Page<Book> getAllActiveBookPagination(Integer pageNo, Integer pageSize, String category, String publisher) {

		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Book> pageBook = null;

		 if (ObjectUtils.isEmpty(category) && ObjectUtils.isEmpty(publisher)) {
		        // Nếu không có category và publisher, trả về tất cả sách đang hoạt động
			 pageBook = bookRepository.findByIsActiveTrue(pageable);
		    } else if (!ObjectUtils.isEmpty(category) && !ObjectUtils.isEmpty(publisher)) {
		        // Nếu có cả category và publisher, lọc theo cả hai
		    	pageBook = bookRepository.findByCategoryAndPublisherAndIsActiveTrue(pageable,category, publisher);
		    } else if (!ObjectUtils.isEmpty(category)) {
		        // Nếu chỉ có category, lọc theo category
		    	pageBook = bookRepository.findByCategoryAndIsActiveTrue(pageable,category);
		    } else if (!ObjectUtils.isEmpty(publisher)) {
		        // Nếu chỉ có publisher, lọc theo publisher
		    	pageBook = bookRepository.findByPublisherAndIsActiveTrue(pageable,publisher);
		    }
		return pageBook;
	}
}
