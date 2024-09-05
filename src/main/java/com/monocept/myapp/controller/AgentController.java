package com.monocept.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.AgentRequestDto;
import com.monocept.myapp.dto.AgentResponseDto;
import com.monocept.myapp.dto.ClaimRequestDto;
import com.monocept.myapp.dto.ClaimResponseDto;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.ClaimService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/GuardianLifeAssurance")
public class AgentController {

	@Autowired
	private AgentManagementService agentManagementService;

	@Autowired
	private ClaimService claimService;

	@GetMapping("/agents/{agentId}")
	@Operation(summary = "get agent by id", description = " agent")
	public ResponseEntity<AgentResponseDto> getAgentById(@PathVariable long agentId) {
		return new ResponseEntity<AgentResponseDto>(agentManagementService.getAgentById(agentId), HttpStatus.OK);
	}

	@PutMapping("/agents")
	@Operation(summary = "update agent", description = " agent")
	public ResponseEntity<String> updateAgent(@RequestBody AgentRequestDto agentRequestDto) {
		return new ResponseEntity<String>(agentManagementService.updateAgent(agentRequestDto), HttpStatus.OK);
	}

	@PostMapping("/agent/{agentId}/claim")
	public ResponseEntity<ClaimResponseDto> createClaim(@PathVariable Long agentId,
			@RequestBody ClaimRequestDto claimRequestDto) {
		ClaimResponseDto claimResponseDto = claimService.createClaim(agentId, claimRequestDto);
		return ResponseEntity.ok(claimResponseDto);
	}
	
	 @GetMapping("/agent/{agentId}/claim")
	    public ResponseEntity<List<ClaimResponseDto>> getClaimsByAgentId(@PathVariable Long agentId) {
	        List<ClaimResponseDto> claimResponseDtos = claimService.getClaimsByAgentId(agentId);
	        return ResponseEntity.ok(claimResponseDtos);
	    }
}
