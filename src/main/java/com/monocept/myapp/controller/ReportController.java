package com.monocept.myapp.controller;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.monocept.myapp.dto.AgentResponseDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.PaymentResponseDto;
import com.monocept.myapp.entity.AgentEarnings;
import com.monocept.myapp.entity.WithdrawalRequest;
import com.monocept.myapp.enums.WithdrawalRequestStatus;
import com.monocept.myapp.service.AgentEarningsReportService;
import com.monocept.myapp.service.AgentEarningsService;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.AgentReportService;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.CustomerReportService;
import com.monocept.myapp.service.PaymentReportService;
import com.monocept.myapp.service.PaymentService;
import com.monocept.myapp.service.WithdrawalReportService;
import com.monocept.myapp.service.WithdrawalService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("GuardianLifeAssurance")
public class ReportController {
	
	@Autowired
	private CustomerManagementService customerManagementService;
	
	@Autowired
    private AgentManagementService agentManagementService;
	
	@Autowired
	private CustomerReportService customerReportService;
	
	@Autowired
	private AgentReportService agentReportService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PaymentReportService paymentReportService;
	
	@Autowired
	private WithdrawalService withdrawalService;
	
	@Autowired
	private WithdrawalReportService withdrawalReportService;
	@Autowired
	private AgentEarningsService agentEarningsService;
	
	@Autowired
	private AgentEarningsReportService agentEarningsReportService;
	
	
	@GetMapping("/customers/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Generate PDF report for customers", description = "Generate a PDF report of all customers based on search filters")
    public ResponseEntity<byte[]> generateCustomerReportPdf(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "customerId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "isActive", required = false) Boolean isActive) throws DocumentException, IOException {
        
        List<CustomerResponseDto> customers = customerManagementService.getAllCustomersWithFilters(page, size, sortBy, direction, name, city, state, isActive).getContent();

        ByteArrayInputStream pdfStream = customerReportService.generateCustomerReport(customers);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=CustomerReport.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfStream.readAllBytes());
    }
    @GetMapping("/agents/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Generate Agent Report", description = "Generate a PDF report of agents with filters")
    public ResponseEntity<byte[]> generateAgentReport(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "agentId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "isActive", required = false) Boolean isActive,
            @RequestParam(name = "name", required = false) String name) throws DocumentException, IOException {

        PagedResponse<AgentResponseDto> agentResponse = agentManagementService.getAllAgents(page, size, sortBy, direction, city, state, isActive, name);
        List<AgentResponseDto> agents = agentResponse.getContent();

        ByteArrayInputStream reportStream = agentReportService.generateAgentReport(agents);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=AgentReport.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportStream.readAllBytes());
    }
    
    @GetMapping("/payments/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Generate PDF report for payments", description = "Generate a PDF report of payments based on search filters")
    public ResponseEntity<byte[]> generatePaymentReportPdf(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "paymentId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "minAmount", required = false) Double minAmount,
            @RequestParam(name = "maxAmount", required = false) Double maxAmount,
            @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) LocalDateTime endDate,
            @RequestParam(name = "customerId", required = false) String customerId,
            @RequestParam(name = "paymentId", required = false) Long paymentId) throws DocumentException, IOException {

        List<PaymentResponseDto> payments = paymentService.getAllPaymentsWithFilters(page, size, sortBy, direction, minAmount, maxAmount, startDate, endDate, customerId, paymentId).getContent();

        ByteArrayInputStream pdfStream = paymentReportService.generatePaymentReport(payments);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=PaymentReport.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @GetMapping("withdrawal/pdf")
    @Operation(summary = "Generate Withdrawal Report", description = "Generate a PDF report of withdrawal requests with filters and pagination")
    public ResponseEntity<byte[]> generateWithdrawalReport(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "withdrawalRequestId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "customerId", required = false) Long customerId,
            @RequestParam(name = "agentId", required = false) Long agentId,
            @RequestParam(name = "status", required = false) WithdrawalRequestStatus status,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws DocumentException, IOException {

        PagedResponse<WithdrawalRequest> withdrawals = withdrawalService.getWithdrawalsWithFilters(customerId, agentId, status, fromDate, toDate, page, size, sortBy, direction);
        ByteArrayInputStream pdfStream = withdrawalReportService.generateWithdrawalReport(withdrawals.getContent());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=WithdrawalReport.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfStream.readAllBytes());
    }
    @GetMapping("/commission-withdrawal/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Generate Commission Withdrawal Report", description = "Generate a PDF report of commission withdrawals with pagination and filters")
    public ResponseEntity<byte[]> generateCommissionWithdrawalReport(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "withdrawalRequestId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "agentId", required = false) Long agentId,
            @RequestParam(name = "status", required = false) WithdrawalRequestStatus status,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws DocumentException, IOException {

        PagedResponse<WithdrawalRequest> commissionWithdrawals = withdrawalService.getCommissionWithdrawalsWithFilters(page, size, sortBy, direction, agentId, status, fromDate, toDate);
        ByteArrayInputStream pdfStream = withdrawalReportService.generateWithdrawalReport(commissionWithdrawals.getContent());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=CommissionWithdrawalReport.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfStream.readAllBytes());
    }
    @GetMapping("/agent-earnings/pdf")
    public ResponseEntity<byte[]> generateAgentEarningsReport(
            @RequestParam(name = "agentId", required = false) Long agentId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "minAmount", required = false) Double minAmount,
            @RequestParam(name = "maxAmount", required = false) Double maxAmount,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) throws DocumentException, IOException {

        Page<AgentEarnings> earnings = agentEarningsService.getAgentEarningsWithFilters(agentId, status, minAmount, maxAmount, fromDate, toDate, page, size);
        ByteArrayInputStream pdfStream = agentEarningsReportService.generateAgentEarningsReport(earnings.getContent());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=AgentEarningsReport.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfStream.readAllBytes());
    }
    
    




}
