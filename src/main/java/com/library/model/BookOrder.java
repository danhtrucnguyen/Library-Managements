package com.library.model;


import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class BookOrder {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderId;

    private LocalDateTime orderDate;
    
    private String formattedOrderDate;

    @ManyToOne
    private User user;

    private String status;

    private String paymentType;	
    
    private int totalAmount; // tổng giá trị đơn hàng
    
    private int totalPrice;
    
    private String formattedTotalAmount;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;

    @OneToMany(mappedBy = "bookOrder", cascade = CascadeType.ALL)
    private List<BookOrderItem> items;  // Danh sách các sản phẩm trong đơn hàng

}
