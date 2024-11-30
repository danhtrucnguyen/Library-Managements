package com.library.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.service.UserService;
//import com.library.service.impl.UserServiceImpl;
import com.library.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String email = request.getParameter("username");

		User userDtls = userRepository.findByEmail(email);

		if (userDtls != null) {

			if (userDtls.getIsEnable()) {

				if (userDtls.getAccountNonLocked()) {

					if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
						userService.increaseFailedAttempt(userDtls);
					} else {
						userService.userAccountLock(userDtls);
						exception = new LockedException("Đăng nhập lần 3 không thành công!! Tài khoản của bạn tạm thời bị khóa");
					}
				} else {

					if (userService.unlockAccountTimeExpired(userDtls)) {
						exception = new LockedException("Tài khoản của bạn đã được mở khóa !! Đăng nhập ngay");
					} else {
						exception = new LockedException("Tài khoản của bạn đã bị khóa!! Hãy thử lại");
					}
				}

			} else {
				exception = new LockedException("Tài khoản của bạn không hoạt động");
			}
		} else {
			exception = new LockedException("Thông tin đăng nhập không chính xác");
		}

		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}