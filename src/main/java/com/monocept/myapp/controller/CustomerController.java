package com.monocept.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.service.CustomerManagementService;


@RestController
@RequestMapping("/GuardianLifeAssurance/customer")
@PreAuthorize("hasRole('Customer')")
public class CustomerController {
	@Autowired
	private CustomerManagementService customerManagementService;
	
	@PutMapping
	public ResponseEntity<String> putMethodName(@RequestBody CustomerRequestDto customerRequestDto) {
		
		return new ResponseEntity<String>(customerManagementService.updateCustomer(customerRequestDto),HttpStatus.OK);
	}
}
