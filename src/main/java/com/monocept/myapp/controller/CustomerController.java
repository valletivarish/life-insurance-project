package com.monocept.myapp.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.ChangePasswordRequestDto;
import com.monocept.myapp.dto.ClaimRequestDto;
import com.monocept.myapp.dto.ClaimResponseDto;
import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.CustomerSideQueryRequestDto;
import com.monocept.myapp.dto.InstallmentPaymentRequestDto;
import com.monocept.myapp.dto.PolicyAccountRequestDto;
import com.monocept.myapp.dto.PolicyAccountResponseDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.enums.DocumentType;
import com.monocept.myapp.service.AuthService;
import com.monocept.myapp.service.ClaimService;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.InstallmentService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/GuardianLifeAssurance/customers")
public class CustomerController {
	@Autowired
	private CustomerManagementService customerManagementService;

	@Autowired
	private ClaimService claimService;

	@Autowired
	private AuthService authService;
	
	@Autowired
	private InstallmentService installmentService;
	
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@PutMapping("change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		return new ResponseEntity<String>(authService.changePassword(changePasswordRequestDto), HttpStatus.OK);
	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/{customerID}")
	@Operation(summary = "Get customer details by ID", description = "Fetch customer details using customer ID")
	public ResponseEntity<CustomerResponseDto> getCustomerIdById(@PathVariable long customerID) {
		return new ResponseEntity<CustomerResponseDto>(customerManagementService.getCustomerIdById(customerID),
				HttpStatus.OK);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@Operation(summary = "Get all customers with pagination and filtering", description = "Retrieve all customers with pagination, sorting, and optional search filters")
	public ResponseEntity<PagedResponse<CustomerResponseDto>> getAllCustomer(
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "5") int size,
	        @RequestParam(name = "sortBy", defaultValue = "customerId") String sortBy,
	        @RequestParam(name = "direction", defaultValue = "ASC") String direction,
	        @RequestParam(name = "name", required = false) String name,
	        @RequestParam(name = "city", required = false) String city,
	        @RequestParam(name = "state", required = false) String state,
	        @RequestParam(name = "isActive", required = false) Boolean isActive) {
	    return new ResponseEntity<>(
	            customerManagementService.getAllCustomersWithFilters(page, size, sortBy, direction, name, city, state, isActive),
	            HttpStatus.OK);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/{customerId}/queries")
	@Operation(summary = "Get all queries for a customer", description = "Retrieve all queries created by a specific customer")
	public ResponseEntity<PagedResponse<QueryResponseDto>> getAllQueriesByCustomer(
			@PathVariable(name = "customerId") long customerId,
			@RequestParam(name = "page", defaultValue = "5") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "queryId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<QueryResponseDto>>(
				customerManagementService.getAllQueriesByCustomer(customerId, page, size, sortBy, direction),
				HttpStatus.OK);
	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("{customerId}/policies")
	@Operation(summary = "Get all policies for a customer", description = "Retrieve all policies purchased by a customer with pagination")
	public ResponseEntity<PagedResponse<PolicyAccountResponseDto>> getAllPoliciesByCustomerId(
			@PathVariable(name = "customerId") long customerId,
			@RequestParam(name = "page", defaultValue = "5") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "policyNo") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<PolicyAccountResponseDto>>(
				customerManagementService.getAllPoliciesByCustomerId(customerId, page, size, sortBy, direction),
				HttpStatus.OK);

	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("{customerId}/policies/{policyId}")
	@Operation(summary = "Get specific policy by ID", description = "Fetch a policy using the customer ID and policy ID")
	public ResponseEntity<PolicyAccountResponseDto> getPolicyById(@PathVariable(name = "customerId") long customerId,
			@PathVariable(name = "policyId") long policyId) {
		return new ResponseEntity<PolicyAccountResponseDto>(
				customerManagementService.getPolicyById(customerId, policyId), HttpStatus.OK);

	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping("/{customerId}/documents")
	@Operation(summary = "Upload a document for a customer", description = "Upload a document for a customer such as an Aadhaar card or PAN card")
	public ResponseEntity<String> uploadDocument(@RequestParam(name = "document") MultipartFile file,
			@RequestParam(name = "documentName") DocumentType documentName,
			@PathVariable(name = "customerId") long customerId) throws IOException {
		return new ResponseEntity<String>(customerManagementService.uploadDocument(file, documentName, customerId),
				HttpStatus.OK);
	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping("/{customerId}/query")
	@Operation(summary = "Create a query for a customer", description = "Create a query for customer-related issues or inquiries")
	public ResponseEntity<String> createCustomerQuery(@PathVariable(name = "customerId") long customerId,
			@RequestBody CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		return new ResponseEntity<String>(
				customerManagementService.createCustomerQuery(customerId, customerSideQueryRequestDto),
				HttpStatus.CREATED);
	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping("/{customerId}/policies")
	@Operation(summary = "Buy a policy for a customer", description = "Purchase a policy for a customer")
	public ResponseEntity<Long> buyPolicy(@RequestBody PolicyAccountRequestDto accountRequestDto,
			@PathVariable(name = "customerId") long customerId) {
		Long policyId = customerManagementService.processPolicyPurchase(accountRequestDto, customerId);
		return new ResponseEntity<>(policyId, HttpStatus.OK);
	}
	@PreAuthorize("hasRole('CUSTOMER')")
	@PutMapping("/{customerId}/query")
	@Operation(summary = "Update a query for a customer", description = "Update an existing query for a customer")
	public ResponseEntity<String> updateCustomerQuery(@PathVariable(name = "customerId") long customerId,
			@RequestBody CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		return new ResponseEntity<String>(
				customerManagementService.updateCustomerQuery(customerId, customerSideQueryRequestDto), HttpStatus.OK);
	}

	@PutMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Update customer details", description = "Update customer information such as name or contact details")
	public ResponseEntity<String> updateCustomer(@RequestBody CustomerRequestDto customerRequestDto) {
		return new ResponseEntity<String>(customerManagementService.updateCustomer(customerRequestDto), HttpStatus.OK);
	}

	@DeleteMapping("/{customerId}/queries/{queryId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Delete a customer query", description = "Delete a customer query using customer ID and query ID")
	public ResponseEntity<String> deleteCustomerQuery(@PathVariable(name = "customerId") long customerId,
			@PathVariable(name = "queryId") long queryId) {
		return new ResponseEntity<String>(customerManagementService.deleteQuery(customerId, queryId), HttpStatus.OK);

	}

	@DeleteMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deactivate a customer", description = "Deactivate a customer by their ID")
	public ResponseEntity<String> deactivateCustomer(@PathVariable Long CustomerID) {
		return new ResponseEntity<String>(customerManagementService.deactivateCustomer(CustomerID), HttpStatus.OK);
	}

	@PostMapping("{customerId}/claims")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ClaimResponseDto> createClaim(@PathVariable Long customerId,
			@RequestBody ClaimRequestDto claimRequestDto) {
		ClaimResponseDto claimResponseDto = claimService.createCustomerClaim(customerId, claimRequestDto);
		return ResponseEntity.ok(claimResponseDto);
	}

	@GetMapping("/{customerId}/claims")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<List<ClaimResponseDto>> getClaimsByCustomerId(@PathVariable Long customerId) {
		List<ClaimResponseDto> claims = claimService.getAllClaimsByCustomerId(customerId);
		return new ResponseEntity<>(claims, HttpStatus.OK);
	}
	@DeleteMapping("{customerId}/policies/cancel/{policyNo}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<String> policyCancel(@PathVariable(name = "customerId") long customerId,@PathVariable(name = "policyNo") long policyNo){
		return new ResponseEntity<String>(customerManagementService.cancelPolicy(customerId,policyNo),HttpStatus.OK);
	}
	@PostMapping("{customerId}/policies/installments/{installmentId}/pay")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<String> payInstallment(
	        @PathVariable Long customerId, 
	        @PathVariable Long installmentId, 
	        @RequestBody InstallmentPaymentRequestDto paymentRequest) {

	    paymentRequest.setInstallmentId(installmentId);
	    paymentRequest.setCustomerId(customerId);

	    String response = installmentService.processInstallmentPayment(paymentRequest);
	    return ResponseEntity.ok(response);
	}

	
	

}
