package com.library.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.library.model.BookOrder;
import com.library.model.BookOrderItem;
import com.library.model.User;
import com.library.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UserService userService;

	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("danhtrucsaison@gmail.com", "Book Library");
		helper.setTo(reciepentEmail);

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {

		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();

		return siteUrl.replace(request.getServletPath(), "");

	}

	String msg = null;;

	public Boolean sendMailForBookOrder(BookOrder order,String status) throws Exception
	{
		
		msg="<p>Xin chào [[name]],</p>"
				+ "<p>Cảm ơn bạn đã đặt hàng <b>[[orderStatus]]</b>.</p>"
				+ "<p><b>Chi tiết sản phẩm:</b></p>"
//				StringBuilder itemDetails = new StringBuilder();
//			    for (BookOrderItem item : order.getItems()) {
//			        itemDetails.append("<p>Tên sách: ").append(item.getBook().getBookName()).append("</p>")
//			            .append("<p>Danh mục: ").append(item.getBook().getCategory()).append("</p>")
//			            .append("<p>Số lượng: ").append(item.getQuantity()).append("</p>")
//			            .append("<p>Giá sách: ").append(item.getPrice()).append("</p>");
//			    }
//
//			    // Thêm thông tin chi tiết sản phẩm vào thông điệp email
//			    msg += itemDetails.toString()
			    
				+ "<p>Số tiền thanh toán : [[formattedTotalAmount]]</p>"
				+ "<p>Hình thức thanh toán : [[paymentType]]</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("danhtrucsaison@gmail.com", "Bookly");
		helper.setTo(order.getOrderAddress().getEmail());

		msg=msg.replace("[[name]]",order.getOrderAddress().getFirstName());
		msg=msg.replace("[[orderStatus]]",status);
//		msg=msg.replace("[[bookName]]", order.getBook().getTitle());
//		msg=msg.replace("[[category]]", order.getBook().getCategory());
//		msg=msg.replace("[[quantity]]", order.getQuantity().toString());
//		msg=msg.replace("[[price]]", order.getPrice().toString());
		msg=msg.replace("[[formattedTotalAmount]]", order.getFormattedTotalAmount());
		msg=msg.replace("[[paymentType]]", order.getPaymentType());
		
		helper.setSubject("Trạng thái đơn hàng");
		helper.setText(msg, true);
		mailSender.send(message);
		return true;
	}

	public User getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		User user = userService.getUserByEmail(email);
		return user;
	}
}
