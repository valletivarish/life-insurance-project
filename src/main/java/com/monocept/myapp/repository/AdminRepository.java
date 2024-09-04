package com.monocept.myapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Admin;
import com.monocept.myapp.entity.User;

public interface AdminRepository extends JpaRepository<Admin, Long> {


}
