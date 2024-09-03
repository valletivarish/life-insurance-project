package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
