package com.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.Author;

public interface AuthorRepository extends JpaRepository<Author,Long>{

}
