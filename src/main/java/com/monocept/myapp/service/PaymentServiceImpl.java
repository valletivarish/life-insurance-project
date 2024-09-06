package com.monocept.myapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.PaymentResponseDto;
import com.monocept.myapp.entity.Payment;
import com.monocept.myapp.repository.PaymentRepository;
import com.monocept.myapp.util.PagedResponse;

@Service
public class PaymentServiceImpl implements PaymentService{

	@Autowired
    private PaymentRepository paymentRepository;

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
        dto.setCustomerId(payment.getCustomerId());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }

}
