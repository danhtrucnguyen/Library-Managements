package com.library.service.impl;



import java.time.LocalDateTime;
//import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import com.library.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private BookOrderRepository orderRepository;
	
	@Autowired
	private BookOrderItemRepository bookOrderItemRepository;

	@Autowired
	private CartRepository cartRepository;

	@Override
	public void saveOrder(Integer userid, OrderRequest orderRequest) {

//		List<Cart> carts = cartRepository.findByUserId(userid);
//
//		for (Cart cart : carts) {
//
//			BookOrder order = new BookOrder();
//
//			order.setOrderId(UUID.randomUUID().toString());
//			order.setOrderDate(LocalDate.now());
//
//			order.setBook(cart.getBook());
//			order.setPrice(cart.getBook().getDiscountPrice());
//
//			order.setQuantity(cart.getQuantity());
//			order.setUser(cart.getUser());
//
//			order.setStatus(OrderStatus.IN_PROGRESS.getName());
//			order.setPaymentType(orderRequest.getPaymentType());
//
//			OrderAddress address = new OrderAddress();
//			address.setFirstName(orderRequest.getFirstName());
//			address.setLastName(orderRequest.getLastName());
//			address.setEmail(orderRequest.getEmail());
//			address.setMobileNo(orderRequest.getMobileNo());
//			address.setAddress(orderRequest.getAddress());
//			address.setCity(orderRequest.getCity());
//			address.setDistrict(orderRequest.getDistrict());
//			address.setNote(orderRequest.getNote());
//
//			order.setOrderAddress(address);
//
//			orderRepository.save(order);
		
		 List<Cart> carts = cartRepository.findByUserId(userid);

		    // Tạo một đơn hàng duy nhất cho người dùng
		    BookOrder order = new BookOrder();
		    order.setOrderId(UUID.randomUUID().toString());
		    order.setOrderDate(LocalDateTime.now());
		    order.setUser(carts.get(0).getUser()); // Đảm bảo lấy thông tin người dùng từ giỏ hàng
		    order.setStatus(OrderStatus.IN_PROGRESS.getName());
		    order.setPaymentType(orderRequest.getPaymentType());

		    // Thiết lập địa chỉ giao hàng
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

		    // Lưu đơn hàng
		    orderRepository.save(order);

		    // Duyệt qua tất cả các giỏ hàng của người dùng và thêm các sản phẩm vào đơn hàng
		    for (Cart cart : carts) {
		        // Lưu từng sản phẩm của giỏ hàng vào đơn hàng
		        BookOrderItem orderItem = new BookOrderItem();
		        orderItem.setBookOrder(order);  // Liên kết với đơn hàng
		        orderItem.setBook(cart.getBook());
		        orderItem.setQuantity(cart.getQuantity());
		        orderItem.setPrice(cart.getBook().getDiscountPrice());

		        // Lưu chi tiết sản phẩm vào cơ sở dữ liệu
		        bookOrderItemRepository.save(orderItem);
		    }

		
	}

	@Override
	public List<BookOrder> getOrdersByUser(Integer userId) {
		List<BookOrder> orders = orderRepository.findByUserId(userId);
		return orders;
	}

	@Override
	public Boolean updateOrderStatus(Integer id, String status) {
		Optional<BookOrder> findById = orderRepository.findById(id);
		if (findById.isPresent()) {
			BookOrder bookOrder = findById.get();
			bookOrder.setStatus(status);
			orderRepository.save(bookOrder);
			return true;
		}
		return false;
	}
	
	@Override
	public List<BookOrder> getAllOrders() {
		return orderRepository.findAll();
	}

}