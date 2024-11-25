package com.library.service;

import java.util.List;

import com.library.model.Publisher;

public interface PublisherService {
	
	public Publisher savePublisher(Publisher category);

	public Boolean existPublisher(String name);

	public List<Publisher> getAllPublisher();

	public Boolean deletePublisher(int id);

	public Publisher getPublisherById(int id);

	public List<Publisher> getAllActivePublisher();
	
}
