package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

}
