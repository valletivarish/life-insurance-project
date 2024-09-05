package com.monocept.myapp.service;

import java.util.List;

import com.monocept.myapp.dto.ClaimRequestDto;
import com.monocept.myapp.dto.ClaimResponseDto;

public interface ClaimService {

	ClaimResponseDto createClaim(Long agentId, ClaimRequestDto claimRequestDto);

	List<ClaimResponseDto> getClaimsByAgentId(Long agentId);

	ClaimResponseDto createCustomerClaim(Long customerId, ClaimRequestDto claimRequestDto);

	List<ClaimResponseDto> getAllClaimsByCustomerId(Long customerId);

	String approveClaim(Long claimId);

	String rejectClaim(Long claimId);

}
