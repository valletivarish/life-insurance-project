package com.monocept.myapp.dto;

import lombok.Data;

import com.monocept.myapp.enums.ClaimStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class ClaimRequestDto {

    @NotNull(message = "Policy number is required")
    private Long policyNo;

    @NotNull(message = "Claim amount is required")
    private Double claimAmount;

    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name can't exceed 100 characters")
    private String bankName;

    @NotBlank(message = "Branch name is required")
    @Size(max = 100, message = "Branch name can't exceed 100 characters")
    private String branchName;

    @NotBlank(message = "Bank account number is required")
    @Size(min = 8, max = 20, message = "Bank account number must be between 8 and 20 characters")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC code is required")
    @Size(min = 11, max = 11, message = "IFSC code must be 11 characters")
    private String ifscCode;

    private ClaimStatus status;
}
