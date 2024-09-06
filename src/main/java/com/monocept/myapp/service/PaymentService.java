package com.monocept.myapp.service;

import java.time.LocalDateTime;

import com.monocept.myapp.dto.PaymentResponseDto;
import com.monocept.myapp.util.PagedResponse;

public interface PaymentService {

	PagedResponse<PaymentResponseDto> getAllPaymentsWithFilters(int page, int size, String sortBy, String direction,
			Double minAmount, Double maxAmount, LocalDateTime startDate,
			LocalDateTime endDate, String customerId, Long paymentId);

}
