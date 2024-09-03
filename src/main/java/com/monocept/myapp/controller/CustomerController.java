package com.monocept.myapp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


@RestController
@RequestMapping("/GuardianLifeAssurance/customers")
//@PreAuthorize("hasRole('Customer')")
public class CustomerController {
	@Autowired
	private CustomerManagementService customerManagementService;
	
	@PutMapping
	public ResponseEntity<String> updateCustomer(@RequestBody CustomerRequestDto customerRequestDto){
		return new ResponseEntity<String>(customerManagementService.updateCustomer(customerRequestDto),HttpStatus.OK);
	}
	
	@GetMapping("/{customerID}")
	public ResponseEntity<CustomerResponseDto> getCustomerIdById(@PathVariable long customerID) {
		return new ResponseEntity<CustomerResponseDto>(customerManagementService.getCustomerIdById(customerID),
				HttpStatus.OK);
	}
	@GetMapping
	public ResponseEntity<PagedResponse<CustomerResponseDto>> getAllCustomer(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "customerId") String sortBy,
			@RequestParam(name = "direction") String direction) {
		return new ResponseEntity<PagedResponse<CustomerResponseDto>>(
				customerManagementService.getAllCustomer(page, size, sortBy, direction), HttpStatus.OK);
	}
	@DeleteMapping
	public ResponseEntity<String> deactivateCustomer(@PathVariable Long CustomerID) {
		return new ResponseEntity<String>(customerManagementService.deactivateCustomer(CustomerID), HttpStatus.OK);
	}
	
	@PostMapping("/{customerId}/documents")
	public ResponseEntity<String> uploadDocument(@RequestParam(name = "document")MultipartFile file,@RequestParam(name = "documentName") String documentName,@PathVariable(name = "customerId") long customerId) throws IOException{
		return new ResponseEntity<String>(customerManagementService.uploadDocument(file,documentName,customerId),HttpStatus.OK);
		
	}
}
