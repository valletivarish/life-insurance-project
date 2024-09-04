package com.monocept.myapp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.CustomerSideQueryRequestDto;
import com.monocept.myapp.dto.InterestCalculatorRequestDto;
import com.monocept.myapp.dto.InterestCalculatorResponseDto;
import com.monocept.myapp.dto.PolicyAccountRequestDto;
import com.monocept.myapp.dto.PolicyAccountResponseDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.enums.DocumentType;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.InsuranceManagementService;
import com.monocept.myapp.util.PagedResponse;

@RestController
@RequestMapping("/GuardianLifeAssurance/customers")
//@PreAuthorize("hasRole('Customer')")
public class CustomerController {
	@Autowired
	private CustomerManagementService customerManagementService;

	@Autowired
	private InsuranceManagementService insuranceManagementService;

	@PutMapping
	public ResponseEntity<String> updateCustomer(@RequestBody CustomerRequestDto customerRequestDto) {
		return new ResponseEntity<String>(customerManagementService.updateCustomer(customerRequestDto), HttpStatus.OK);
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
	public ResponseEntity<String> uploadDocument(@RequestParam(name = "document") MultipartFile file,
			@RequestParam(name = "documentName") DocumentType documentName,
			@PathVariable(name = "customerId") long customerId) throws IOException {
		return new ResponseEntity<String>(customerManagementService.uploadDocument(file, documentName, customerId),
				HttpStatus.OK);
	}

	@PostMapping("/customers/{customerId}/query")
	public ResponseEntity<String> createCustomerQuery(@PathVariable(name = "customerId") long customerId,
			@RequestBody CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		return new ResponseEntity<String>(
				customerManagementService.createCustomerQuery(customerId, customerSideQueryRequestDto),
				HttpStatus.CREATED);
	}

	@PutMapping("/customers/{customerId}/query")
	public ResponseEntity<String> updateCustomerQuery(@PathVariable(name = "customerId") long customerId,
			@RequestBody CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		return new ResponseEntity<String>(
				customerManagementService.updateCustomerQuery(customerId, customerSideQueryRequestDto), HttpStatus.OK);
	}

	@GetMapping("/customers/{customerId}/queries")
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

	@DeleteMapping("/customers/{customerId}/queries/{queryId}")
	public ResponseEntity<String> deleteCustomerQuery(@PathVariable(name = "customerId") long customerId,
			@PathVariable(name = "queryId") long queryId) {
		return new ResponseEntity<String>(customerManagementService.deleteQuery(customerId, queryId), HttpStatus.OK);

	}

	@PostMapping("/InterestCalculator")
	public ResponseEntity<InterestCalculatorResponseDto> calculateInterest(
			@RequestBody InterestCalculatorRequestDto interestCalculatorDto) {
		System.out.println(interestCalculatorDto);
		return new ResponseEntity<InterestCalculatorResponseDto>(
				insuranceManagementService.calculateInterest(interestCalculatorDto), HttpStatus.OK);
	}

	@PostMapping("/customers/{customerId}/policies")
	public ResponseEntity<Long> buyPolicy(@RequestBody PolicyAccountRequestDto accountRequestDto,
			@PathVariable(name = "customerId") long customerId) {
		System.out.println(accountRequestDto);
		System.out.println(customerId);
		return new ResponseEntity<Long>(customerManagementService.buyPolicy(accountRequestDto, customerId),
				HttpStatus.OK);

	}
	@GetMapping("customers/{customerId}/policies")
	public ResponseEntity<PagedResponse<PolicyAccountResponseDto>> getAllPoliciesByCustomerId(
			@PathVariable(name = "customerId") long customerId,
			@RequestParam(name = "page", defaultValue = "5") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "policyNo") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction){
		return new ResponseEntity<PagedResponse<PolicyAccountResponseDto>>(customerManagementService.getAllPoliciesByCustomerId(customerId, page, size, sortBy, direction),HttpStatus.OK);
		
	}
	@GetMapping("customers/{customerId}/policies/{policyId}")
	public ResponseEntity<PolicyAccountResponseDto> getPolicyById(@PathVariable(name = "customerId")long customerId,@PathVariable(name = "policyId")long policyId){
		return new ResponseEntity<PolicyAccountResponseDto>(customerManagementService.getPolicyById(customerId,policyId),HttpStatus.OK);
		
	}
}
