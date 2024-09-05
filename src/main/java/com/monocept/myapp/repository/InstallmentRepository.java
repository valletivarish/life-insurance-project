package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Installment;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {

}
