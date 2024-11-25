package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.library.model.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher,Integer >  {
	
	public Boolean existsByName(String name);
	
	public List<Publisher> findByIsActiveTrue();
	
}
