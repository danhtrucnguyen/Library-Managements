package com.library.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.library.model.OrderRequest;
import com.library.model.BookOrder;

public interface OrderService {

	public void saveOrder(Integer userid,OrderRequest orderRequest) throws Exception;
	
	public List<BookOrder> getOrdersByUser(Integer userId);
	
	public BookOrder updateOrderStatus(Integer id,String status);

	public List<BookOrder> getAllOrders();
	
	public Map<Integer, Integer> getMonthlyRevenue(int year);
	
	public Map<Integer, Integer> getDailyRevenue(int year, int month);
	
	public Map<String, Long> getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate);
	
	public BookOrder getOrdersByOrderId(String orderId);
	
}
