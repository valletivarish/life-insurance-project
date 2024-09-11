package com.monocept.myapp.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.AgentRequestDto;
import com.monocept.myapp.dto.AgentResponseDto;
import com.monocept.myapp.dto.ChangePasswordRequestDto;
import com.monocept.myapp.dto.DocumentResponseDto;
import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.dto.PaymentResponseDto;
import com.monocept.myapp.dto.QueryReplyDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.entity.Document;
import com.monocept.myapp.enums.DocumentType;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.AuthService;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.DashboardService;
import com.monocept.myapp.service.DocumentService;
import com.monocept.myapp.service.EmployeeManagementService;
import com.monocept.myapp.service.PaymentService;
import com.monocept.myapp.util.ImageUtil;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/GuardianLifeAssurance/employees")
public class EmployeeController {
	
	@Autowired
	private EmployeeManagementService employeeManagementService;
	
	@Autowired
	private CustomerManagementService customerManagementService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private DocumentService documentService;
	
	@PutMapping("change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto){
		return new ResponseEntity<String>(authService.changePassword(changePasswordRequestDto),HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@PutMapping
	@Operation(summary = "Update employee details", description = "Update the details of an existing employee")
	public ResponseEntity<EmployeeResponseDto> updateEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
		return new ResponseEntity<EmployeeResponseDto>(employeeManagementService.updateEmployee(employeeRequestDto),
				HttpStatus.OK);
	}
	
	@GetMapping("/employees/{employeesId}")
	@Operation(summary = "get employee by id", description = "")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<EmployeeResponseDto> getemployeesIdById(@PathVariable long employeesId) {
		return new ResponseEntity<EmployeeResponseDto>(employeeManagementService.getemployeesIdById(employeesId),
				HttpStatus.OK);
	}
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@PutMapping("/documents/{documentId}/approve")
	public ResponseEntity<String> approveDocument(@PathVariable(name = "documentId") int documentId) {
		String response = employeeManagementService.verifyDocument(documentId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@PutMapping("/documents/{documentId}/reject")
	public ResponseEntity<String> rejectDocument(@PathVariable(name = "documentId") int documentId) {

		String response = employeeManagementService.rejectDocument(documentId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@PreAuthorize("hasRole('EMPLOYEE')")
	@GetMapping("/customer/queries")
	public ResponseEntity<PagedResponse<QueryResponseDto>> getAllQueries(
	    @RequestParam(name = "page", defaultValue = "0") int page,
	    @RequestParam(name = "size", defaultValue = "5") int size,
	    @RequestParam(name = "sortBy", defaultValue = "queryId") String sortBy,
	    @RequestParam(name = "direction", defaultValue = "asc") String direction,
	    @RequestParam(name = "title", required = false) String search, 
	    @RequestParam(name = "resolved", required = false) Boolean resolved 
	) {
	    PagedResponse<QueryResponseDto> response = customerManagementService.getAllQueries(page, size, sortBy, direction, search, resolved);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@PreAuthorize("hasRole('EMPLOYEE')")
	@PostMapping("/customer/queries/{queryId}/respond")
	public ResponseEntity<String> respondToQuery(@PathVariable(name = "queryId") long queryId, @RequestBody QueryReplyDto queryReplyDto) {
		String response = customerManagementService.respondToQuery(queryId,queryReplyDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Autowired
	private AgentManagementService agentManagementService;

	@GetMapping("/agents")
	@Operation(summary = "Get all agents", description = "")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<PagedResponse<AgentResponseDto>> getAllAgents(
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "5") int size,
	        @RequestParam(name = "sortBy", defaultValue = "agentId") String sortBy,
	        @RequestParam(name = "direction", defaultValue = "ASC") String direction,
	        @RequestParam(name = "city", required = false) String city,
	        @RequestParam(name = "state", required = false) String state,
	        @RequestParam(name = "isActive", required = false) Boolean isActive,
	        @RequestParam(name = "name", required = false) String name) {
		System.out.println(page);
		System.out.println(size);
		System.out.println(sortBy);
		System.out.println(direction);
	    return new ResponseEntity<PagedResponse<AgentResponseDto>>(
	            agentManagementService.getAllAgents(page, size, sortBy, direction, city, state, isActive, name), 
	            HttpStatus.OK);
	}
	private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(DateTimeFormatter.ofPattern("yyyy-MM-dd"),
			DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	@GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<PagedResponse<PaymentResponseDto>> generatePaymentReportPdf(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "paymentId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "minAmount", required = false) Double minAmount,
            @RequestParam(name = "maxAmount", required = false) Double maxAmount,
            @RequestParam(name = "startDate", defaultValue = "#{T(java.time.LocalDate).now().minusDays(30).toString()}") String startDate,
            @RequestParam(name = "endDate", defaultValue = "#{T(java.time.LocalDate).now().toString()}") String endDate,
            @RequestParam(name = "customerId", required = false) String customerId,
            @RequestParam(name = "paymentId", required = false) Long paymentId) {
		
		LocalDateTime fromDate = parseDate(startDate).atStartOfDay();
		LocalDateTime toDate = parseDate(endDate).atTime(23, 59, 59);

        PagedResponse<PaymentResponseDto> payments = paymentService.getAllPaymentsWithFilters(page, size, sortBy, direction, minAmount, maxAmount, fromDate, toDate, customerId, paymentId);
        return new ResponseEntity<PagedResponse<PaymentResponseDto>>(payments,HttpStatus.OK);
    }
	private LocalDate parseDate(String dateStr) {
		for (DateTimeFormatter formatter : FORMATTERS) {
			try {
				return LocalDate.parse(dateStr, formatter);
			} catch (DateTimeParseException e) {
			}
		}
		return LocalDate.now();
	}

	@PostMapping("/agents")
	@Operation(summary = "create agent", description = "creating agent")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<String> createAgent(@RequestBody AgentRequestDto agentRequestDto) {
		return new ResponseEntity<String>(agentManagementService.createAgent(agentRequestDto), HttpStatus.CREATED);
	}
	
	@GetMapping("counts")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Long>> getEmployeeDashboardCounts() {

        return new ResponseEntity<Map<String,Long>>(dashboardService.getEmployeeDashboardCount(),HttpStatus.OK);
    }
	
	@GetMapping("/documents")
    public ResponseEntity<PagedResponse<DocumentResponseDto>> getAllDocuments(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "documentId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "verified", required = false) Boolean verified) {

        PagedResponse<DocumentResponseDto> response = documentService.getAllDocuments(page, size, sortBy, direction, verified);
        return ResponseEntity.ok(response);
	}

	@GetMapping("/documents/{documentId}/content")
	public ResponseEntity<byte[]> getDocumentContent(@PathVariable int documentId) {
	    Document document = documentService.getDocumentById(documentId);
	    byte[] content = ImageUtil.decompressFile(document.getContent()); 

	    return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(content);
	}






	
	
}
