package com.monocept.myapp.dto;

import lombok.Data;

@Data
public class ClaimRequestDto {
    private long policyNo;
    private double claimAmount;
    private String claimReason;
}
