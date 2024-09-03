package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}
