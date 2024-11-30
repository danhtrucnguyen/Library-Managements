package com.library.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OrderRequest {
	
	private Integer id;

	private String firstName;

	private String lastName;

	private String email;

	private String mobileNo;

	private String address;
	
	private String district;

	private String city;

	private String note;
	
	private String paymentType;
}
