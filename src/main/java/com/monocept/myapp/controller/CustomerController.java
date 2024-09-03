package com.monocept.myapp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/GuardianLifeAssurance/customer")
@PreAuthorize("hasRole('Customer')")
public class CustomerController {
	
}
