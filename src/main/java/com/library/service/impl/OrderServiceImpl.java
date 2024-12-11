package com.library.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
//import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.model.Cart;
import com.library.model.OrderAddress;
import com.library.model.OrderRequest;
import com.library.model.BookOrder;
import com.library.model.BookOrderItem;
import com.library.repository.CartRepository;
import com.library.repository.BookOrderItemRepository;
import com.library.repository.BookOrderRepository;
import com.library.service.OrderService;
import com.library.util.CommonUtil;
import com.library.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private BookOrderRepository orderRepository;

	@Autowired
	private BookOrderItemRepository bookOrderItemRepository;

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CommonUtil commonUtil;

	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception {

		List<Cart> carts = cartRepository.findByUserId(userid);

		BookOrder order = new BookOrder();
		String orderId = "BLY-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-"
				+ (int) (Math.random() * 1000);
		order.setOrderId(orderId);
		order.setOrderDate(LocalDateTime.now());
		order.setUser(carts.get(0).getUser()); 
		order.setStatus(OrderStatus.IN_PROGRESS.getName());
		order.setPaymentType(orderRequest.getPaymentType());

		DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String formattedDateTime = order.getOrderDate().format(formatters);
		order.setFormattedOrderDate(formattedDateTime);

		int totalAmount = carts.stream().mapToInt(cart -> cart.getBook().getDiscountPrice() * cart.getQuantity()).sum();
		int shippingFee = 20000;
		totalAmount += shippingFee;
		order.setTotalAmount(totalAmount);

		int totalPrice = carts.stream().mapToInt(cart -> cart.getBook().getDiscountPrice() * cart.getQuantity()).sum();
		order.setTotalPrice(totalPrice);

		DecimalFormat formatter = new DecimalFormat("#,###");
		String formattedTotalAmount = formatter.format(totalAmount);
		order.setFormattedTotalAmount(formattedTotalAmount);

		OrderAddress address = new OrderAddress();
		address.setFirstName(orderRequest.getFirstName());
		address.setLastName(orderRequest.getLastName());
		address.setEmail(orderRequest.getEmail());
		address.setMobileNo(orderRequest.getMobileNo());
		address.setAddress(orderRequest.getAddress());
		address.setCity(orderRequest.getCity());
		address.setDistrict(orderRequest.getDistrict());
		address.setNote(orderRequest.getNote());

		order.setOrderAddress(address);

		BookOrder saveOrder = orderRepository.save(order);
		commonUtil.sendMailForBookOrder(saveOrder, "success");


		for (Cart cart : carts) {
			BookOrderItem orderItem = new BookOrderItem();
			orderItem.setBookOrder(order); 
			orderItem.setBook(cart.getBook());
			orderItem.setQuantity(cart.getQuantity());
			orderItem.setPrice(cart.getBook().getDiscountPrice());

			bookOrderItemRepository.save(orderItem);
		}

	}

	@Override
	public List<BookOrder> getOrdersByUser(Integer userId) {
		List<BookOrder> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
		return orders;
	}

	@Override
	public BookOrder updateOrderStatus(Integer id, String status) {
		Optional<BookOrder> findById = orderRepository.findById(id);
		if (findById.isPresent()) {
			BookOrder bookOrder = findById.get();
			bookOrder.setStatus(status);
			BookOrder updateOrder = orderRepository.save(bookOrder);
			return updateOrder;
		}
		return null;
	}

	@Override
	public List<BookOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Map<Integer, Integer> getMonthlyRevenue(int year) {
		List<Object[]> revenueData = orderRepository.getMonthlyRevenue(year);

		Map<Integer, Integer> monthlyRevenue = new HashMap<>();
		for (Object[] data : revenueData) {
			int month = (int) data[0]; 
			int revenue = ((Number) data[1]).intValue(); 
			monthlyRevenue.put(month, revenue);
		}

		for (int i = 1; i <= 12; i++) {
			monthlyRevenue.putIfAbsent(i, 0);
		}

		return monthlyRevenue;
	}

	@Override
	public Map<Integer, Integer> getDailyRevenue(int year, int month) {
		Map<Integer, Integer> dailyRevenue = new HashMap<>();

		List<BookOrder> orders = orderRepository.findOrdersByMonthAndYear(year, month);

		for (BookOrder order : orders) {
			LocalDateTime orderDate = order.getOrderDate();

			if (orderDate.getYear() == year && orderDate.getMonthValue() == month) {
				int day = orderDate.getDayOfMonth();
				int totalPrice = order.getTotalPrice(); 

				dailyRevenue.put(day, dailyRevenue.getOrDefault(day, 0) + totalPrice);
			}
		}

		return dailyRevenue;
	}
	
	@Override
	public Map<String, Long> getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = orderRepository.countOrdersByStatusAndDateRange(startDate, endDate);
        Map<String, Long> statistics = new HashMap<>();
        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(status, count);
        }
        return statistics;
    }
	
	@Override
	public BookOrder getOrdersByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId);
	}
}