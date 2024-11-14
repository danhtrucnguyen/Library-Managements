package com.library.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.library.security.CustomUserDetailService;

@Configuration
public class WebSecurityConfig {

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Autowired
	@Lazy
	private AuthFailureHandlerImpl authenticationFailureHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailService();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean 
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.csrf(csrf->csrf.disable()).cors(cors->cors.disable())
				.authorizeHttpRequests(req->req.requestMatchers("/user/**").hasRole("USER")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/**").permitAll())
				.formLogin(form->form.loginPage("/signin")
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/")
						.failureHandler(authenticationFailureHandler)
						.successHandler(authenticationSuccessHandler))
				.logout(logout->logout.permitAll());
		
		return http.build();
	}

//	@Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//            .cors(cors -> cors.disable())
//            .authorizeHttpRequests(req -> req
//                .requestMatchers("/signin", "/register", "/css/**", "/js/**").permitAll()  // Cho phép truy cập vào trang login và các tài nguyên tĩnh
//                .requestMatchers("/user/**").hasRole("USER")  // Bảo vệ các trang yêu cầu vai trò USER
//                .requestMatchers("/admin/**").hasRole("ADMIN")  // Bảo vệ các trang yêu cầu vai trò ADMIN
//                .anyRequest().authenticated())  // Các trang khác yêu cầu xác thực
//            .formLogin(form -> form
//                .loginPage("/signin")  // Trang đăng nhập tùy chỉnh
//                .loginProcessingUrl("/login")  // URL xử lý đăng nhập
//                .failureUrl("/signin?error=true")  // Định hướng đến trang login với tham số error nếu đăng nhập thất bại
//                .failureHandler(authenticationFailureHandler)
//                .successHandler(authenticationSuccessHandler))  // Xử lý đăng nhập thành công
//            .logout(logout -> logout.permitAll());  // Cho phép logout
//
//        return http.build();
//    }
}


