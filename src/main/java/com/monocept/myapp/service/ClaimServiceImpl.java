package com.monocept.myapp.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.ClaimRequestDto;
import com.monocept.myapp.dto.ClaimResponseDto;
import com.monocept.myapp.entity.Agent;
import com.monocept.myapp.entity.Claim;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.Installment;
import com.monocept.myapp.entity.PolicyAccount;
import com.monocept.myapp.enums.ClaimStatus;
import com.monocept.myapp.enums.InstallmentStatus;
import com.monocept.myapp.enums.PolicyStatus;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException;
import com.monocept.myapp.repository.AgentRepository;
import com.monocept.myapp.repository.ClaimRepository;
import com.monocept.myapp.repository.CustomerRepository;
import com.monocept.myapp.repository.InstallmentRepository;
import com.monocept.myapp.repository.PolicyRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.util.ImageUtil;
import com.monocept.myapp.util.PagedResponse;

@Service
public class ClaimServiceImpl implements ClaimService {

	@Autowired
	private ClaimRepository claimRepository;

	@Autowired
	private PolicyRepository policyRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private StripeService stripeService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AgentRepository agentRepository;
	
	@Autowired
	private InstallmentRepository installmentRepository;

	@Override
	public ClaimResponseDto createCustomerClaim(Long customerId, ClaimRequestDto claimRequestDto) throws IOException {

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Customer not found"));

		PolicyAccount policyAccount = policyRepository.findById(claimRequestDto.getPolicyNo())
				.orElseThrow(() -> new GuardianLifeAssuranceException("Policy not found"));

		if (!policyAccount.getCustomer().equals(customer)) {
			throw new GuardianLifeAssuranceException("Policy does not belong to the customer");
		}

		Claim claim = new Claim();
		claim.setPolicyAccount(policyAccount);
		claim.setClaimAmount(claimRequestDto.getClaimAmount());
		claim.setClaimReason(claimRequestDto.getClaimReason());
		claim.setDocument(ImageUtil.compressFile(claimRequestDto.getDocument().getBytes()));
		claim.setStatus(ClaimStatus.PENDING);
		claim.setClaimDate(LocalDateTime.now());
		claim.setCustomer(customer);

		Claim savedClaim = claimRepository.save(claim);

		return convertEntityToDto(savedClaim);
	}

	@Override
	public List<ClaimResponseDto> getAllClaimsByCustomerId(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Customer not found"));

		List<Claim> claims = claimRepository.findByCustomer(customer);

		return claims.stream().map(this::convertEntityToDto).collect(Collectors.toList());
	}

	@Override
	public String approveClaim(Long claimId) {
		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Claim not found"));

		if (claim.getStatus() != ClaimStatus.PENDING) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Claim is not in a pending state.");
		}

		PolicyAccount policyAccount = claim.getPolicyAccount();
		double sumAssured = policyAccount.getSumAssured();

//		String stripeToken = claim.getCustomer().getStripeToken();
//		if (stripeToken == null || stripeToken.isEmpty()) {
//			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST,
//					"No Bank details avialable for the customer " + claim.getCustomer().getCustomerId());
//		}
		policyAccount.setStatus(PolicyStatus.CLAIMED);
		List<Installment> unpaidInstallments = installmentRepository.findByInsurancePolicyAndStatus(policyAccount,
				InstallmentStatus.PENDING);
		unpaidInstallments.forEach(installment -> {
			installment.setStatus(InstallmentStatus.CANCELED);
			installmentRepository.save(installment);
		});
		claim.setStatus(ClaimStatus.APPROVED);
		claim.setApprovalDate(LocalDateTime.now());
		claimRepository.save(claim);
		emailService.sendClaimApprovalMail(claim);

		return "Claim approved and payout of sum assured processed.";
	}

	@Override
	public String rejectClaim(Long claimId) {
		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new GuardianLifeAssuranceException("Claim not found"));

		if (claim.getStatus() != ClaimStatus.PENDING) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Claim is not in a pending state.");
		}

		claim.setStatus(ClaimStatus.REJECTED);
		claim.setRejectionDate(LocalDateTime.now());
		claimRepository.save(claim);

		return "Claim rejected.";
	}

	@Override
	public PagedResponse<ClaimResponseDto> getAllClaimsWithFilters(int page, int size, String sortBy, String direction,
			ClaimStatus status, Long customerId, Long policyNo) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);

		Page<Claim> claims = claimRepository.findAllWithFilters(status, customerId, policyNo, pageRequest);
		List<ClaimResponseDto> claimResponseDtos = claims.getContent().stream().map(this::convertEntityToDto)
				.collect(Collectors.toList());

		return new PagedResponse<>(claimResponseDtos, claims.getNumber(), claims.getSize(), claims.getTotalElements(),
				claims.getTotalPages(), claims.isLast());
	}

	private ClaimResponseDto convertEntityToDto(Claim claim) {
		ClaimResponseDto dto = new ClaimResponseDto();
		dto.setClaimId(claim.getClaimId());
		dto.setPolicyNo(claim.getPolicyAccount().getPolicyNo());
		dto.setClaimAmount(claim.getClaimAmount());
		dto.setClaimReason(claim.getClaimReason());
		dto.setStatus(claim.getStatus());
		dto.setApprovalDate(claim.getApprovalDate());
		dto.setRejectionDate(claim.getRejectionDate());
		dto.setClaimDate(claim.getClaimDate());
		return dto;
	}

	@Override
	public PagedResponse<ClaimResponseDto> getAgentClaims(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);

		// Fetch claims by agent ID
		Page<Claim> claimsPage = claimRepository.findClaimsByAgentId(getAgentFromSecurityContext().getAgentId(),
				pageRequest);

		List<ClaimResponseDto> claims = claimsPage.getContent().stream().map(this::convertEntityToDto)
				.collect(Collectors.toList());

		return new PagedResponse<>(claims, claimsPage.getNumber(), claimsPage.getSize(), claimsPage.getTotalElements(),
				claimsPage.getTotalPages(), claimsPage.isLast());
	}

	private Agent getAgentFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			return agentRepository.findByUser(
				    userRepository.findByUsernameOrEmail(
				        userDetails.getUsername(), 
				        userDetails.getUsername()
				    ).orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException("User not found"))
				);
		}
		throw new GuardianLifeAssuranceException.UserNotFoundException("agent not found");

}
}
