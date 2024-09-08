package com.monocept.myapp.service;

import java.io.ByteArrayInputStream;

import com.itextpdf.text.DocumentException;
import com.monocept.myapp.dto.InstallmentPaymentRequestDto;

public interface InstallmentService {

	ByteArrayInputStream processInstallmentPayment(InstallmentPaymentRequestDto paymentRequest) throws DocumentException;

}
