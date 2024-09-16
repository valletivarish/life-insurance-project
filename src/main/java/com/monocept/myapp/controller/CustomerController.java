package com.monocept.myapp.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.monocept.myapp.dto.ChangePasswordRequestDto;
import com.monocept.myapp.dto.ClaimRequestDto;
import com.monocept.myapp.dto.ClaimResponseDto;
import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.CustomerSideQueryRequestDto;
import com.monocept.myapp.dto.DocumentRequestDto;
import com.monocept.myapp.dto.DocumentResponseDto;
import com.monocept.myapp.dto.InsuranceSchemeResponseDto;
import com.monocept.myapp.dto.PolicyAccountRequestDto;
import com.monocept.myapp.dto.PolicyAccountResponseDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.entity.Document;
import com.monocept.myapp.entity.Installment;
import com.monocept.myapp.entity.InsuranceScheme;
import com.monocept.myapp.entity.Payment;
import com.monocept.myapp.entity.PolicyAccount;
import com.monocept.myapp.enums.DocumentType;
import com.monocept.myapp.service.AuthService;
import com.monocept.myapp.service.ClaimService;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.DocumentService;
import com.monocept.myapp.service.GenerateReceiptService;
import com.monocept.myapp.service.InstallmentService;
import com.monocept.myapp.service.InsuranceManagementService;
import com.monocept.myapp.util.ImageUtil;
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
	
	@Autowired
	private InsuranceManagementService insuranceManagementService;
	
	@Autowired
	private GenerateReceiptService generateReceiptService;
	
	@Autowired
	private DocumentService documentService;
	



	@PutMapping("change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		return new ResponseEntity<String>(authService.changePassword(changePasswordRequestDto), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('AGENT')")
	@GetMapping("/{customerID}")
	@Operation(summary = "Get customer details by ID", description = "Fetch customer details using customer ID")
	public ResponseEntity<CustomerResponseDto> getCustomerIdById(@PathVariable long customerID) {
		return new ResponseEntity<CustomerResponseDto>(customerManagementService.getCustomerIdById(customerID),
				HttpStatus.OK);
	}

	@PutMapping("/activate/{customerId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<String> activateCustomer(@PathVariable long customerId) {
		return new ResponseEntity<String>(customerManagementService.activateCustomer(customerId), HttpStatus.OK);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('AGENT')")
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
		return new ResponseEntity<>(customerManagementService.getAllCustomersWithFilters(page, size, sortBy, direction,
				name, city, state, isActive), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/{customerId}/queries")
	@Operation(summary = "Get all queries for a customer", description = "Retrieve all queries created by a specific customer")
	public ResponseEntity<PagedResponse<QueryResponseDto>> getAllQueriesByCustomer(
			@PathVariable(name = "customerId") long customerId,
			@RequestParam(name = "page", defaultValue = "0") int page,
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
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "policyNo") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<PolicyAccountResponseDto>>(
				customerManagementService.getAllPoliciesByCustomerId(customerId, page, size, sortBy, direction),
				HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@GetMapping("policies")
	@Operation(summary = "Get all policies for a customer", description = "Retrieve all policies purchased by a customer with pagination")
	public ResponseEntity<PagedResponse<PolicyAccountResponseDto>> getAllPolicies(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "policyNo") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<PolicyAccountResponseDto>>(
				customerManagementService.getAllPolicies(page, size, sortBy, direction), HttpStatus.OK);

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
	@PostMapping("/{customerId}/queries")
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
//	@PreAuthorize("hasRole('CUSTOMER')")
//	@PutMapping("/{customerId}/query")
//	@Operation(summary = "Update a query for a customer", description = "Update an existing query for a customer")
//	public ResponseEntity<String> updateCustomerQuery(@PathVariable(name = "customerId") long customerId,
//			@RequestBody CustomerSideQueryRequestDto customerSideQueryRequestDto) {
//		return new ResponseEntity<String>(
//				customerManagementService.updateCustomerQuery(customerId, customerSideQueryRequestDto), HttpStatus.OK);
//	}

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

	@DeleteMapping("/{customerId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@Operation(summary = "Deactivate a customer", description = "Deactivate a customer by their ID")
	public ResponseEntity<String> deactivateCustomer(@PathVariable Long customerId) {
		return new ResponseEntity<String>(customerManagementService.deactivateCustomer(customerId), HttpStatus.OK);
	}

	@PostMapping("{customerId}/claims")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ClaimResponseDto> createClaim(
	        @PathVariable Long customerId,
	        @ModelAttribute ClaimRequestDto claimRequestDto) throws IOException {

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
	public ResponseEntity<String> policyCancel(@PathVariable(name = "customerId") long customerId,
			@PathVariable(name = "policyNo") long policyNo) {
		return new ResponseEntity<String>(customerManagementService.cancelPolicy(customerId, policyNo), HttpStatus.OK);
	}

//	@PostMapping("{customerId}/policies/installments/{installmentId}/pay")
//	@PreAuthorize("hasRole('CUSTOMER')")
//	public ResponseEntity<byte[]> payInstallment(@PathVariable Long customerId, @PathVariable Long installmentId,
//			@RequestBody InstallmentPaymentRequestDto paymentRequest) throws DocumentException, IOException {
//
//		paymentRequest.setInstallmentId(installmentId);
//		paymentRequest.setCustomerId(customerId);
//
//		long receiptStream = installmentService.processInstallmentPayment(paymentRequest);
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Disposition", "inline; filename=InstallmentReceipt.pdf");
//
//		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
//				.body(receiptStream.readAllBytes());
//
//	}
//	
	@GetMapping("/details")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Map<String, Object>> getCurrentCustomerDetails() {
	    String currentUserEmail = getCurrentUserEmail();
	    Map<String, Object> userDetails = authService.getUserByEmail(currentUserEmail);
	    if (userDetails == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<>(userDetails, HttpStatus.OK);
	}
	private String getCurrentUserEmail() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
	        return ((UserDetails) authentication.getPrincipal()).getUsername();
	    }
	    return null; 
	}
	@GetMapping("/plans/{planId}/schemes")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Page<InsuranceSchemeResponseDto>> getSchemesByPlanId(
	        @PathVariable Long planId,
	        @RequestParam(name = "page",defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "1") int size) {
	    
	    Page<InsuranceSchemeResponseDto> schemes = insuranceManagementService.getSchemesByPlanId(planId, page, size);
	    
	    return ResponseEntity.ok(schemes);
	}
	

	
	@GetMapping("/schemes/{schemeId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<byte[]> getschemeContent(@PathVariable long schemeId) {
	    InsuranceScheme scheme = insuranceManagementService.getSchemeImageById(schemeId);
	    byte[] content = ImageUtil.decompressFile(scheme.getSchemeImage()); 

	    return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(content);
	}
	@PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/age")
    public ResponseEntity<Integer> getCustomerAge() {
        String username = getCurrentUserEmail();
        int age = customerManagementService.calculateCustomerAgeByUser(username);

        return ResponseEntity.ok(age);
    }

	
	@GetMapping("installments/receipt/{installmentId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<byte[]> generateReceipt(@PathVariable Long installmentId) throws IOException {
        try {
            Installment installment = installmentService.findInstallmentById(installmentId);
            Payment payment = installmentService.getPaymentByInstallment(installment);
            PolicyAccount policyAccount = installment.getInsurancePolicy();

            ByteArrayInputStream pdfStream = generateReceiptService.generateReceipt(installment, payment, policyAccount);

            // Set response headers for the PDF
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=receipt.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfStream.readAllBytes());

        } catch (DocumentException e) {
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }
    
    @GetMapping("/{customerId}/documents")
	@PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PagedResponse<DocumentResponseDto>> getAllDocumentsOfCustomer(@PathVariable(name = "customerId") long customerId,@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "documentId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "verified", required = false) Boolean verified){
    	PagedResponse<DocumentResponseDto> response = documentService.getAllDocuments(customerId,page, size, sortBy, direction, verified);
        return ResponseEntity.ok(response);
    	
    }
    
    @GetMapping("documents/{documentId}/download")
	@PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable(name = "documentId") int documentId){
    	Document document = documentService.getDocumentById(documentId);
	    byte[] content = ImageUtil.decompressFile(document.getContent()); 

	    return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(content);
    }
    
    @PutMapping("documents")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> updateDocument(@RequestParam(name = "document") MultipartFile multipartFile, @RequestBody DocumentRequestDto documentRequestDto) throws IOException{
    	documentRequestDto.setDocument(multipartFile.getBytes());
    	return new ResponseEntity<String>(documentService.updateDocument(documentRequestDto),HttpStatus.OK);
    }
	
    @GetMapping("documents/types")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<DocumentType>> getDocumentTypes () {
    	List<DocumentType> documentTypes = Arrays.asList(DocumentType.values());
        return ResponseEntity.ok(documentTypes);
    }
    
}
