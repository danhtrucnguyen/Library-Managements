package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private int id;

	@Column(length = 500)
	private String bookName;

	@Column(length = 5000)
	private String description;

	private String author;

	private String category;

	private String publisher;
	
	private Integer price;

	private int stock;

	private String image;

	private int discount;

	private Integer discountPrice;

	private String isbn;

	private Boolean isActive;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	public void calculateDiscountPrice() {
		if (price != null && discount >= 0 && discount <= 100) {
			this.discountPrice = price - (price * discount / 100);
		} else {
			this.discountPrice = this.price;
		}
	}
	
	private double averageRating;

}
