package com.monocept.myapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.PaymentResponseDto;
import com.monocept.myapp.entity.Agent;
import com.monocept.myapp.entity.Payment;
import com.monocept.myapp.entity.PolicyAccount;
import com.monocept.myapp.exception.GuardianLifeAssuranceException;
import com.monocept.myapp.repository.AgentRepository;
import com.monocept.myapp.repository.PaymentRepository;
import com.monocept.myapp.repository.PolicyRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.util.PagedResponse;

@Service
public class PaymentServiceImpl implements PaymentService{

	@Autowired
    private PaymentRepository paymentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AgentRepository agentRepository;
	


    @Override
    public PagedResponse<PaymentResponseDto> getAllPaymentsWithFilters(int page, int size, String sortBy, String direction,
                                                                       Double minAmount, Double maxAmount,
                                                                       LocalDateTime startDate, LocalDateTime endDate, String customerId, Long paymentId) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Payment> paymentsPage = paymentRepository.findByFilters(minAmount, maxAmount, startDate, endDate, customerId, paymentId, pageable);

        List<PaymentResponseDto> paymentDtos = paymentsPage.getContent().stream()
                .map(payment->convertPaymentToPaymentResponseDto(payment))
                .collect(Collectors.toList());

        return new PagedResponse<>(paymentDtos, paymentsPage.getNumber(), paymentsPage.getSize(),
                paymentsPage.getTotalElements(), paymentsPage.getTotalPages(), paymentsPage.isLast());
    }

    private PaymentResponseDto convertPaymentToPaymentResponseDto(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setAmount(payment.getAmount());
        if(payment.getPolicyAccount()!=null) {
        	dto.setPolicyNo(payment.getPolicyAccount().getPolicyNo());
        }
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(toLocalDate(payment.getPaymentDate()));
        return dto;
    }
    public static LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null; 
    }

    public PagedResponse<PaymentResponseDto> getPaymentsByAgentWithPagination(int page, int size, String sortBy, String direction, LocalDateTime fromDate, LocalDateTime toDate) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        long agentId = getAgentFromSecurityContext().getAgentId();  

        Page<Payment> paymentPage = paymentRepository.findPaymentsByAgentWithDateRange(agentId, fromDate, toDate, pageable);


        List<PaymentResponseDto> paymentDtos = paymentPage.getContent().stream()
                .map(this::convertPaymentToPaymentResponseDto)
                .collect(Collectors.toList());
        System.out.println(paymentDtos);
        return new PagedResponse<>(paymentDtos, paymentPage.getNumber(), paymentPage.getSize(), paymentPage.getTotalElements(), paymentPage.getTotalPages(), paymentPage.isLast());
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
