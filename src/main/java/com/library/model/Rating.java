package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int score;  // Điểm số đánh giá (ví dụ 1-5)
    
    private String review;  // Nội dung nhận xét

    private LocalDateTime createdAt;  // Thời gian tạo đánh giá

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;  // Mối quan hệ với sản phẩm

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // Mối quan hệ với người dùng

}