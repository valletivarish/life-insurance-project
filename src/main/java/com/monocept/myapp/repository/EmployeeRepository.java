package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
