package com.monocept.myapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Claim;
import com.monocept.myapp.entity.Customer;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

	List<Claim> findByAgentAgentId(Long agentId);

	List<Claim> findByCustomer(Customer customer);

}
