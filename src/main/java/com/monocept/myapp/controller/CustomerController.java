package com.monocept.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.service.CustomerManagementService;

@RestController
@RequestMapping("/GuardianLifeAssurance/customer")
@PreAuthorize("hasRole('Customer')")
public class CustomerController {
	@Autowired
	private CustomerManagementService customerManagementService;
}
