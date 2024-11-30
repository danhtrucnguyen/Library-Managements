package com.library.service;

import java.util.List;

import com.library.model.OrderRequest;
import com.library.model.BookOrder;

public interface OrderService {

	public void saveOrder(Integer userid,OrderRequest orderRequest);
	
	public List<BookOrder> getOrdersByUser(Integer userId);
	
	public Boolean updateOrderStatus(Integer id,String status);

}
