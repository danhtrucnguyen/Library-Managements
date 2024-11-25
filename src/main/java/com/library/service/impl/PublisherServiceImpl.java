package com.library.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.library.model.Publisher;
import com.library.repository.PublisherRepository;
import com.library.service.PublisherService;
@Service
public class PublisherServiceImpl implements PublisherService {
	
	@Autowired
	private PublisherRepository publisherRepository;

	@Override
	public Publisher savePublisher(Publisher publisher) {
		return publisherRepository.save(publisher);
	}

	@Override
	public List<Publisher> getAllPublisher() {
		return publisherRepository.findAll();
	}

	@Override
	public Boolean existPublisher(String name) {
		return publisherRepository.existsByName(name);
	}

	@Override
	public Boolean deletePublisher(int id) {
		Publisher publisher = publisherRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(publisher)) {
			publisherRepository.delete(publisher);
			return true;
		}
		return false;
	}

	@Override
	public Publisher getPublisherById(int id) {
		Publisher publisher = publisherRepository.findById(id).orElse(null);
		return publisher;
	}

	@Override
	public List<Publisher> getAllActivePublisher() {
		List<Publisher> publishers = publisherRepository.findByIsActiveTrue();
		return publishers;
	}

}
