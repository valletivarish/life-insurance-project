package com.monocept.myapp.dto;

import com.monocept.myapp.enums.ClaimStatus;

import lombok.Data;

@Data
public class ClaimResponseDto {
    private long claimId;
    private long policyNo;
    private double claimAmount;
    private String claimReason;
    private ClaimStatus status;
}

