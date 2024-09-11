package com.monocept.myapp.controller;

import java.time.LocalDateTime;

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
import com.monocept.myapp.dto.ChangePasswordRequestDto;
import com.monocept.myapp.entity.AgentEarnings;
import com.monocept.myapp.service.AgentEarningsService;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.AuthService;
import com.monocept.myapp.service.WithdrawalService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/GuardianLifeAssurance")
public class AgentController {

	@Autowired
	private AgentManagementService agentManagementService;

	@Autowired
	private AuthService authService;
	
	@Autowired
	private WithdrawalService withdrawalService;
	
	@Autowired
	private AgentEarningsService agentEarningsService;

	@PutMapping("change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		return new ResponseEntity<String>(authService.changePassword(changePasswordRequestDto), HttpStatus.OK);
	}

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
}
