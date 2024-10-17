package com.monocept.myapp.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
import com.monocept.myapp.dto.ClaimResponseDto;
import com.monocept.myapp.dto.CommissionResponseDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.InsuranceSchemeResponseDto;
import com.monocept.myapp.dto.PaymentResponseDto;
import com.monocept.myapp.dto.PolicyAccountResponseDto;
import com.monocept.myapp.dto.ReferralEmailRequestDto;
import com.monocept.myapp.dto.WithdrawalResponseDto;
import com.monocept.myapp.entity.AgentEarnings;
import com.monocept.myapp.enums.CommissionType;
import com.monocept.myapp.enums.WithdrawalRequestStatus;
import com.monocept.myapp.service.AgentEarningsService;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.ClaimService;
import com.monocept.myapp.service.CommissionService;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.InsuranceManagementService;
import com.monocept.myapp.service.PaymentService;
import com.monocept.myapp.service.PolicyService;
import com.monocept.myapp.service.WithdrawalService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/GuardianLifeAssurance")
public class AgentController {

	@Autowired
	private AgentManagementService agentManagementService;

	
	@Autowired
	private WithdrawalService withdrawalService;
	
	@Autowired
	private AgentEarningsService agentEarningsService;
	
	@Autowired
	private CommissionService commissionService;
	
	@Autowired
	private CustomerManagementService customerManagementService;
	
	@Autowired
	private PolicyService policyService;
	
	@Autowired
	private ClaimService claimService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private InsuranceManagementService insuranceManagementService;
	


	@GetMapping("/agents/{agentId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('AGENT')")
	@Operation(summary = "get agent by id", description = " agent")
	public ResponseEntity<AgentResponseDto> getAgentById(@PathVariable long agentId) {
		return new ResponseEntity<AgentResponseDto>(agentManagementService.getAgentById(agentId), HttpStatus.OK);
	}

	@PutMapping("/agents")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('AGENT')")
	public ResponseEntity<String> updateAgent(@RequestBody AgentRequestDto agentRequestDto) {
		return new ResponseEntity<String>(agentManagementService.updateAgent(agentRequestDto), HttpStatus.OK);
	}

	@GetMapping("/agents/earnings")
    public ResponseEntity<Page<AgentEarnings>> getAgentEarningsReport(
            @RequestParam(name = "agentId", required = false) Long agentId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "minAmount", required = false) Double minAmount,
            @RequestParam(name = "maxAmount", required = false) Double maxAmount,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<AgentEarnings> earnings = agentEarningsService.getAgentEarningsWithFilters(agentId, status, minAmount, maxAmount, fromDate, toDate, page, size);
        return ResponseEntity.ok(earnings);
    }
	
	@PostMapping("/agent/{agentId}/withdrawals")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<String> createAgentWithdrawalRequest(@PathVariable long agentId,
                                                               @RequestParam double amount,
                                                               @RequestParam String stripeToken) {
        withdrawalService.createAgentWithdrawalRequest(agentId, amount, stripeToken);
        return ResponseEntity.ok("Withdrawal request created successfully");
    }
	@GetMapping("agent/commission-withdrawal")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<PagedResponse<WithdrawalResponseDto>> generateCommissionWithdrawal(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "withdrawalRequestId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "status", required = false) WithdrawalRequestStatus status,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

		
        return new ResponseEntity<PagedResponse<WithdrawalResponseDto>>(withdrawalService.getCommissionWithdrawalsWithFilters(page, size, sortBy, direction,status, fromDate, toDate),HttpStatus.OK);
    }
	@GetMapping("agent/commissions")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<PagedResponse<CommissionResponseDto>> getAllCommissions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "commissionId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "agentId", required = false) Long agentId,
            @RequestParam(name = "commissionType", required = false) CommissionType commissionType,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate from,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate to,
            @RequestParam(name = "amount", required = false) Double amount) {


        PagedResponse<CommissionResponseDto> commissions = commissionService.getCommissionsWithFilters(
                page, size, sortBy, direction, commissionType, from, to, amount);
        
        return new ResponseEntity<>(commissions, HttpStatus.OK);
    }
	
	@GetMapping("agent/customers")
	@PreAuthorize("hasRole('AGENT')")
	@Operation(summary = "Get all customers with pagination and filtering", description = "Retrieve all customers with pagination, sorting, and optional search filters")
	public ResponseEntity<PagedResponse<CustomerResponseDto>> getAllCustomerByAgent(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "customerId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "ASC") String direction,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "city", required = false) String city,
			@RequestParam(name = "state", required = false) String state,
			@RequestParam(name = "isActive", required = false) Boolean isActive) {
		return new ResponseEntity<>(customerManagementService.getAllCustomersByAgentWithFilters(page, size, sortBy, direction,
				name, city, state, isActive), HttpStatus.OK);
	}
	@GetMapping("agent/policies")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Get all policies for the agent", description = "Retrieve all policies associated with the agent, with pagination and filtering.")
    public ResponseEntity<PagedResponse<PolicyAccountResponseDto>> getAllPoliciesByAgent(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "policyNo") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "policyNumber", required = false) Long policyNumber,
            @RequestParam(name = "premiumType", required = false) String premiumType,
            @RequestParam(name = "status", required = false) String status) {


        PagedResponse<PolicyAccountResponseDto> response = policyService.getAllPoliciesByAgentWithFilters(page, size, sortBy, direction, policyNumber, premiumType, status);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@GetMapping("/agent/policies/payments")
	@PreAuthorize("hasRole('AGENT')")
	public ResponseEntity<PagedResponse<PaymentResponseDto>> getAgentPolicyPayments(
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "10") int size,
	        @RequestParam(name = "sortBy", defaultValue = "paymentDate") String sortBy,
	        @RequestParam(name = "direction", defaultValue = "ASC") String direction,
	        @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
	        @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

	    LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
	    LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : null;

	    PagedResponse<PaymentResponseDto> response = paymentService.getPaymentsByAgentWithPagination(page, size, sortBy, direction, fromDateTime, toDateTime);
	    return ResponseEntity.ok(response);
	}

	
	@GetMapping("/agent/policies/claims")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<PagedResponse<ClaimResponseDto>> getAgentPolicyClaims(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "claimDate") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction) {

        PagedResponse<ClaimResponseDto> response = claimService.getAgentClaims(page, size, sortBy, direction);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/agent/profile")
	@PreAuthorize("hasRole('AGENT')")
	public ResponseEntity<AgentResponseDto> getAgentProfile(){
		return new ResponseEntity<AgentResponseDto>(agentManagementService.getAgentProfile(),HttpStatus.OK);
	}
	
	@GetMapping("agent/plans/{planId}/schemes")
	@PreAuthorize("hasRole('AGENT')")
	public ResponseEntity<List<InsuranceSchemeResponseDto>> getSchemesByPlanId(
	        @PathVariable Long planId) {
	    
	    List<InsuranceSchemeResponseDto> schemes = insuranceManagementService.getSchemesByPlanId(planId);
	    
	    return ResponseEntity.ok(schemes);
	}
	@PostMapping("/agent/send-recommendation-email")
    public ResponseEntity<String> sendRecommendation(@RequestBody ReferralEmailRequestDto referralEmailRequestDto) {
        return new ResponseEntity<String>(agentManagementService.sendRecommendationEmail(referralEmailRequestDto),HttpStatus.OK);
       
    }
	
	
}
