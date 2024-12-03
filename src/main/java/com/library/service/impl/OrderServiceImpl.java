package com.library.service.impl;



import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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


		
		 List<Cart> carts = cartRepository.findByUserId(userid);

		    // Tạo một đơn hàng duy nhất cho người dùng
		    BookOrder order = new BookOrder();
		    String orderId = "BLY-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + (int)(Math.random() * 1000);
		    order.setOrderId(orderId);
		    order.setOrderDate(LocalDateTime.now());
		    order.setUser(carts.get(0).getUser()); // Đảm bảo lấy thông tin người dùng từ giỏ hàng
		    order.setStatus(OrderStatus.IN_PROGRESS.getName());
		    order.setPaymentType(orderRequest.getPaymentType());
		    
		    
		    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		    String formattedDateTime = order.getOrderDate().format(formatters);
            order.setFormattedOrderDate(formattedDateTime);
            
		    int totalAmount = carts.stream()
		            .mapToInt(cart -> cart.getBook().getDiscountPrice() * cart.getQuantity())
		            .sum();
		    int shippingFee = 20000;
		    totalAmount += shippingFee;
		    order.setTotalAmount(totalAmount);
		    
		 // Định dạng tổng giá trị đơn hàng
		    DecimalFormat formatter = new DecimalFormat("#,###");
		    String formattedTotalAmount = formatter.format(totalAmount);
		    order.setFormattedTotalAmount(formattedTotalAmount);


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
		List<BookOrder> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
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