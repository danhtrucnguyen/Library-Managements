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

		if (userDtls.getIsEnable()) {

			if (userDtls.getAccountNonLocked()) {

				if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
					userService.increaseFailedAttempt(userDtls);
				} else {
					userService.userAccountLock(userDtls);
					exception = new LockedException("Your account is locked !! failed attempt 3");
				}
			} else {

				if (userService.unlockAccountTimeExpired(userDtls)) {
					exception = new LockedException("Your account is unlocked !! Please try to login");
				} else {
					exception = new LockedException("your account is Locked !! Please try after sometimes");
				}
			}

		} else {
			exception = new LockedException("your account is inactive");
		}
		
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}