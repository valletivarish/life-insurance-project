package com.monocept.myapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.Query;

public interface QueryRepository extends JpaRepository<Query, Long> {

	Page<Query> findByCustomer(Customer customer, PageRequest pageRequest);

	Page<Query> findAllByResolvedFalse(PageRequest pageRequest);

}
