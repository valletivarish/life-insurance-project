package com.monocept.myapp.dto;

import java.sql.Date;

import com.monocept.myapp.enums.ClaimStatus;

import lombok.Data;

@Data
public class ClaimResponseDto {

    private Long claimId;

    private Long policyNo;

    private Double claimAmount;

    private String bankName;

    private String branchName;

    private String bankAccountNumber;

    private String ifscCode;

    private Date claimDate;

    private ClaimStatus status;

    private String agentName; 
}
