package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByBookId(Integer bookId);  // Lấy tất cả đánh giá của một sản phẩm
}
