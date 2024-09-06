package com.monocept.myapp.dto;

import java.time.LocalDate;
import java.util.List;

import com.monocept.myapp.entity.Address;
import com.monocept.myapp.enums.PolicyStatus;
import com.monocept.myapp.enums.PremiumType;

import lombok.Data;

@Data
public class PolicyAccountResponseDto {
	private String customerName;
	private String customerCity;
	private String customerState;
	private String email;
	private String phoneNumber;
	private String agentName;
	private long policyNo;
	private String insurancePlan;
	private String insuranceScheme;
	private LocalDate DateCreated;
	private LocalDate maturityDate;
	private PremiumType premiumType;
	private Address address;
	private double premiumAmount;
	private double profitRatio;
	private double sumAssured;
	private PolicyStatus policyStatus;
	private List<PaymentResponseDto> payments;
	private List<InstallmentResponseDto> installments;

}
