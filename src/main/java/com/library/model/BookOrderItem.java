package com.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_order_id", nullable = false)
    private BookOrder bookOrder;  // Liên kết với BookOrder (đơn hàng)

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;  // Liên kết với Book (sách)


    private Integer quantity;  // Số lượng sách

    private Integer price;  // Giá sách

    public BookOrderItem(BookOrder bookOrder, Book book, Integer quantity, Integer price) {
        this.bookOrder = bookOrder;
        this.book = book;
        this.quantity = quantity;
        this.price = price;
    }
}
