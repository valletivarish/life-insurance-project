package com.monocept.myapp.service;

import com.monocept.myapp.dto.InstallmentPaymentRequestDto;

public interface InstallmentService {

	String processInstallmentPayment(InstallmentPaymentRequestDto paymentRequest);

}
