package com.monocept.myapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.PolicyAccount;

public interface PolicyRepository extends JpaRepository<PolicyAccount, Long> {

	Page<PolicyAccount> findAllByCustomer(PageRequest pageRequest, Customer customer);

}
