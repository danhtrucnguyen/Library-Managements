package com.library.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.library.model.BookOrder;

public interface BookOrderRepository extends JpaRepository<BookOrder, Integer> {

	List<BookOrder> findByUserId(Integer userId);
	
	List<BookOrder> findByUserIdOrderByOrderDateDesc(Integer userId);
	
	@Query("SELECT MONTH(bo.orderDate) AS month, SUM(bo.totalPrice) AS revenue " +
		       "FROM BookOrder bo " +
		       "WHERE YEAR(bo.orderDate) = :year " +
		       "GROUP BY MONTH(bo.orderDate) " +
		       "ORDER BY MONTH(bo.orderDate)")
	List<Object[]> getMonthlyRevenue(@Param("year") int year);
		
	@Query("SELECT b FROM BookOrder b WHERE YEAR(b.orderDate) = :year AND MONTH(b.orderDate) = :month")
	List<BookOrder> findOrdersByMonthAndYear(@Param("year") int year, @Param("month") int month);
	
	 @Query("SELECT b.status, COUNT(b) FROM BookOrder b WHERE b.orderDate BETWEEN :startDate AND :endDate GROUP BY b.status")
	    List<Object[]> countOrdersByStatusAndDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


	BookOrder findByOrderId(String orderId);
}
