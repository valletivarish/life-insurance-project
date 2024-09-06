package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Commission;

public interface CommissionRepository extends JpaRepository<Commission, Long>{

}
