package com.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher,Integer >  {
	
}
