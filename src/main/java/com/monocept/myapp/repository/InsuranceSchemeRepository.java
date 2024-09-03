package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.InsuranceScheme;

public interface InsuranceSchemeRepository extends JpaRepository<InsuranceScheme, Long> {

//	Page<InsuranceScheme> findByInsurancePlan(InsurancePlan insurancePlan, PageRequest pageRequest);

}
