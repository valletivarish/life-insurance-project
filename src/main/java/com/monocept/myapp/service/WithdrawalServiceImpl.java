package com.monocept.myapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.WithdrawalResponseDto;
import com.monocept.myapp.entity.Agent;
import com.monocept.myapp.entity.AgentEarnings;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.Payment;
import com.monocept.myapp.entity.WithdrawalRequest;
import com.monocept.myapp.enums.WithdrawalRequestStatus;
import com.monocept.myapp.enums.WithdrawalRequestType;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException;
import com.monocept.myapp.repository.AgentRepository;
import com.monocept.myapp.repository.AgentWithdrawalHistoryRepository;
import com.monocept.myapp.repository.WithdrawalRepository;
import com.monocept.myapp.util.PagedResponse;

@Service
public class WithdrawalServiceImpl implements WithdrawalService{
	@Autowired
	private WithdrawalRepository withdrawalRepository;
	
	@Autowired
    private AgentRepository agentRepository;

	@Autowired
	private StripeService stripeService;
	
	@Autowired
	private AgentWithdrawalHistoryRepository agentWithdrawalHistoryRepository;
	
	@Autowired
	private EmailService emailService;

	@Override
	public void approveWithdrawal(long withdrawalId) {
		WithdrawalRequest withdrawalRequest = withdrawalRepository.findById(withdrawalId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Withdrawal request not found with ID: " + withdrawalId));

		if (!withdrawalRequest.getStatus().equals(WithdrawalRequestStatus.PENDING)) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST,
					"Only pending withdrawal requests can be approved.");
		}

//		if (withdrawalRequest.getAmount() > 0) {
//			if (withdrawalRequest.getCustomer() != null) {
//				String latestStripeChargeId = getLatestStripeChargeId(withdrawalRequest.getCustomer());
//				stripeService.processStripeRefund(latestStripeChargeId, withdrawalRequest.getAmount());
//			} else if (withdrawalRequest.getAgent() != null) {
//				processAgentCommissionWithdrawal(withdrawalRequest);
//			}
//		}

		withdrawalRequest.setStatus(WithdrawalRequestStatus.APPROVED);
		withdrawalRequest.setApprovedAt(LocalDateTime.now());
		withdrawalRepository.save(withdrawalRequest);
		emailService.sendWithdrawalApprovalMail(withdrawalRequest);
	}
	private void processAgentCommissionWithdrawal(WithdrawalRequest withdrawalRequest) {
	    Agent agent = withdrawalRequest.getAgent();
	    String stripeToken = agent.getStripeToken(); 

	    if (stripeToken == null || stripeToken.isEmpty()) {
	        throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "No Stripe token found for this agent.");
	    }

	    if (withdrawalRequest.getAmount() > agent.getTotalCommission()) {
	        throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Insufficient commission balance.");
	    }

	    try {
	        stripeService.processAgentPayout(stripeToken, withdrawalRequest.getAmount());

	        double remainingCommission = agent.getTotalCommission() - withdrawalRequest.getAmount();
	        agent.setTotalCommission(remainingCommission);
	        agentRepository.save(agent); 

	        AgentEarnings history = new AgentEarnings();
	        history.setAgent(agent);
	        history.setAmount(withdrawalRequest.getAmount());
	        history.setWithdrawalDate(LocalDateTime.now());
	        history.setStripeToken(stripeToken);
	        history.setStatus("COMPLETED");

	        agentWithdrawalHistoryRepository.save(history);
	    } catch (Exception e) {
	        throw new GuardianLifeAssuranceApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process withdrawal: " + e.getMessage());
	    }
	}



	@Override
	public void rejectWithdrawal(long withdrawalId) {
		WithdrawalRequest withdrawalRequest = withdrawalRepository.findById(withdrawalId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Withdrawal request not found with ID: " + withdrawalId));

		if (!withdrawalRequest.getStatus().equals(WithdrawalRequestStatus.PENDING)) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST,
					"Only pending withdrawal requests can be rejected.");
		}

		withdrawalRequest.setStatus(WithdrawalRequestStatus.REJECTED);
		withdrawalRepository.save(withdrawalRequest);
	}

	private String getLatestStripeChargeId(Customer customer) {
		return customer.getPolicies().stream().flatMap(policy -> policy.getPayments().stream())
				.sorted(Comparator.comparing(Payment::getPaymentDate).reversed()).map(Payment::getChargeId).findFirst()
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"No payment record found for this customer."));
	}
	@Override
	public void createAgentWithdrawalRequest(long agentId, double amount, String stripeToken) {
		Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
                        "Agent not found with ID: " + agentId));

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        agent.setStripeToken(stripeToken);
        agentRepository.save(agent);
        withdrawalRequest.setAgent(agent);
        
        withdrawalRequest.setAmount(amount);
        withdrawalRequest.setRequestDate(LocalDateTime.now());
        withdrawalRequest.setStatus(WithdrawalRequestStatus.PENDING);
        withdrawalRequest.setRequestType(WithdrawalRequestType.COMMISSION_WITHDRAWAL);

        withdrawalRepository.save(withdrawalRequest);
	}

	@Override
    public PagedResponse<WithdrawalRequest> getWithdrawalsWithFilters(Long customerId, Long agentId, 
            WithdrawalRequestStatus status, LocalDate fromDate, LocalDate toDate, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
            
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WithdrawalRequest> withdrawalPage = withdrawalRepository.findWithFilters(customerId, agentId, status, fromDate, toDate, pageable);

        List<WithdrawalRequest> withdrawals = withdrawalPage.getContent();

        return new PagedResponse<>(withdrawals, withdrawalPage.getNumber(), withdrawalPage.getSize(),
                withdrawalPage.getTotalElements(), withdrawalPage.getTotalPages(), withdrawalPage.isLast());
    }
	@Override
    public PagedResponse<WithdrawalResponseDto> getCommissionWithdrawalsWithFilters(int page, int size, String sortBy, String direction, 
            Long agentId, WithdrawalRequestStatus status, LocalDate fromDate, LocalDate toDate) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<WithdrawalRequest> withdrawalPage = withdrawalRepository.findCommissionWithdrawals(agentId, status, fromDate, toDate, pageRequest);
        List<WithdrawalResponseDto> withdrawals = withdrawalPage.getContent().stream().map(withdrawalRequest->convertWithdrawalToDto(withdrawalRequest)).collect(Collectors.toList());

        return new PagedResponse<>(withdrawals, withdrawalPage.getNumber(), withdrawalPage.getSize(), 
                                   withdrawalPage.getTotalElements(), withdrawalPage.getTotalPages(), withdrawalPage.isLast());
    }
	private WithdrawalResponseDto convertWithdrawalToDto(WithdrawalRequest withdrawalRequest) {
		WithdrawalResponseDto responseDto =new WithdrawalResponseDto();
		responseDto.setAgentId(withdrawalRequest.getAgent().getAgentId());
		responseDto.setAmount(withdrawalRequest.getAmount());
		responseDto.setApprovedAt(toLocalDate(withdrawalRequest.getApprovedAt()));
		responseDto.setRequestDate(toLocalDate(withdrawalRequest.getRequestDate()));
		responseDto.setWithdrawalRequestId(withdrawalRequest.getWithdrawalRequestId());
		responseDto.setStatus(withdrawalRequest.getStatus());
		responseDto.setRequestType(withdrawalRequest.getRequestType());
		responseDto.setAgentName(withdrawalRequest.getAgent().getFirstName()+" "+withdrawalRequest.getAgent().getLastName());
		return responseDto;
	}
	
	public static LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null; 
    }


}