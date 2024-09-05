package com.monocept.myapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.ClaimRequestDto;
import com.monocept.myapp.dto.ClaimResponseDto;
import com.monocept.myapp.entity.Agent;
import com.monocept.myapp.entity.Claim;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.PolicyAccount;
import com.monocept.myapp.enums.ClaimStatus;
import com.monocept.myapp.exception.GuardianLifeAssuranceException;
import com.monocept.myapp.repository.AgentRepository;
import com.monocept.myapp.repository.ClaimRepository;
import com.monocept.myapp.repository.CustomerRepository;
import com.monocept.myapp.repository.PolicyRepository;

@Service
public class ClaimServiceImpl implements ClaimService {

	@Autowired
	private ClaimRepository claimRepository;

	@Autowired
	private AgentRepository agentRepository;

	@Autowired
	private PolicyRepository policyRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public ClaimResponseDto createClaim(Long agentId, ClaimRequestDto claimRequestDto) {

		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Agent not found with ID: " + agentId));

		PolicyAccount policy = policyRepository.findById(claimRequestDto.getPolicyNo())
				.orElseThrow(() -> new GuardianLifeAssuranceException(
						"Policy not found with PolicyNo: " + claimRequestDto.getPolicyNo()));

		Claim claim = dtoToEntity(claimRequestDto, policy, agent);

		claimRepository.save(claim);

		return entityToDto(claim);
	}

	@Override
	public List<ClaimResponseDto> getClaimsByAgentId(Long agentId) {
		agentRepository.findById(agentId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Agent not found with ID: " + agentId));

		List<Claim> claims = claimRepository.findByAgentAgentId(agentId);

		return claims.stream().map(this::entityToDto).collect(Collectors.toList());
	}

	public Claim dtoToEntity(ClaimRequestDto claimRequestDto, PolicyAccount policy, Agent agent) {
		Claim claim = new Claim();
		claim.setPolicy(policy);
		claim.setClaimAmount(claimRequestDto.getClaimAmount());
		claim.setBankName(claimRequestDto.getBankName());
		claim.setBranchName(claimRequestDto.getBranchName());
		claim.setBankAccountNumber(claimRequestDto.getBankAccountNumber());
		claim.setIfscCode(claimRequestDto.getIfscCode());
		claim.setStatus(ClaimStatus.PENDING);
		claim.setAgent(agent);
		return claim;
	}

	public ClaimResponseDto entityToDto(Claim claim) {
		ClaimResponseDto claimResponseDto = new ClaimResponseDto();
		claimResponseDto.setClaimId(claim.getClaimId());
		claimResponseDto.setPolicyNo(claim.getPolicy().getPolicyNo());
		claimResponseDto.setClaimAmount(claim.getClaimAmount());
		claimResponseDto.setBankName(claim.getBankName());
		claimResponseDto.setBranchName(claim.getBranchName());
		claimResponseDto.setBankAccountNumber(claim.getBankAccountNumber());
		claimResponseDto.setIfscCode(claim.getIfscCode());
		claimResponseDto.setClaimDate(claim.getDate());
		claimResponseDto.setStatus(claim.getStatus());
		claimResponseDto.setAgentName(claim.getAgent().getFirstName() + " " + claim.getAgent().getLastName());
		return claimResponseDto;
	}

	@Override
	public ClaimResponseDto createCustomerClaim(Long customerId, ClaimRequestDto claimRequestDto) {

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Customer not found"));

		PolicyAccount policyAccount = policyRepository.findById(claimRequestDto.getPolicyNo())
				.orElseThrow(() -> new GuardianLifeAssuranceException("Policy not found"));

		if (!policyAccount.getCustomer().equals(customer)) {
			throw new GuardianLifeAssuranceException("Policy does not belong to the customer");
		}

		Claim claim = new Claim();
		claim.setPolicy(policyAccount);
		claim.setClaimAmount(claimRequestDto.getClaimAmount());
		claim.setBankName(claimRequestDto.getBankName());
		claim.setBranchName(claimRequestDto.getBranchName());
		claim.setBankAccountNumber(claimRequestDto.getBankAccountNumber());
		claim.setIfscCode(claimRequestDto.getIfscCode());
		claim.setStatus(ClaimStatus.PENDING);
		claim.setCustomer(customer);

		Claim savedClaim = claimRepository.save(claim);

		return convertEntityToDto(savedClaim);
	}

	public List<ClaimResponseDto> getClaimsByCustomerId(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Customer not found"));

		return customer.getClaims().stream().map(this::convertEntityToDto).collect(Collectors.toList());
	}

	private ClaimResponseDto convertEntityToDto(Claim claim) {
		ClaimResponseDto dto = new ClaimResponseDto();
		dto.setClaimId(claim.getClaimId());
		dto.setPolicyNo(claim.getPolicy().getPolicyNo());
		dto.setClaimAmount(claim.getClaimAmount());
		dto.setBankName(claim.getBankName());
		dto.setBranchName(claim.getBranchName());
		dto.setBankAccountNumber(claim.getBankAccountNumber());
		dto.setIfscCode(claim.getIfscCode());
		dto.setStatus(claim.getStatus());
		return dto;
	}

	@Override
	public List<ClaimResponseDto> getAllClaimsByCustomerId(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Customer not found"));

		List<Claim> claims = claimRepository.findByCustomer(customer);

		return claims.stream().map(this::convertEntityToDto).collect(Collectors.toList());
	}
}
