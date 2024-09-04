package com.monocept.myapp.dto;

import java.time.LocalDate;
import java.util.List;

import com.monocept.myapp.entity.Installment;
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
	private double totalPremiumAmount;
	private double profitRatio;
	private double sumAssured;
	private List<Installment> installments;

}
