package com.library.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.model.Book;
import com.library.model.Rating;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.RatingRepository;
import com.library.repository.UserRepository;

import com.library.service.RatingService;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Rating addRating(Integer bookId, Integer userId, int score, String review) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Tạo đối tượng Rating mới
		Rating rating = new Rating();
		rating.setBook(book);
		rating.setUser(user);
		rating.setScore(score);
		rating.setReview(review);
		rating.setCreatedAt(LocalDateTime.now());

		// Lưu đánh giá vào cơ sở dữ liệu
		Rating savedRating = ratingRepository.save(rating);

		// Cập nhật điểm trung bình của sản phẩm sau khi thêm đánh giá mới
		updateAverageRating(book);

		return savedRating;
	}

	// Phương thức tính toán điểm trung bình và cập nhật vào sản phẩm

	private void updateAverageRating(Book book) {
		List<Rating> ratings = ratingRepository.findByBookId(book.getId());

		double averageRating = ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);

		// Cập nhật lại điểm trung bình cho sản phẩm
		book.setAverageRating(averageRating);
		bookRepository.save(book);
	}

	// Lấy các đánh giá của sản phẩm
	@Override
	public List<Rating> getRatingsForBook(Integer bookId) {
		List<Rating> ratings = ratingRepository.findByBookId(bookId);

		// Sắp xếp danh sách đánh giá theo thời gian (mới nhất lên đầu)
		ratings.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

		return ratings;

	}

	// Lấy điểm trung bình của sản phẩm
	@Override
	public double getAverageRating(Integer bookId) {
		List<Rating> ratings = ratingRepository.findByBookId(bookId);
	    
	    double averageRating = ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
	    
	    // Làm tròn số trung bình đến 1 chữ số sau dấu phẩy
	    return Double.parseDouble(String.format("%.1f", averageRating));
	}
}
