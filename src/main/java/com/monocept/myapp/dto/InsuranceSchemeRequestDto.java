package com.monocept.myapp.dto;

import java.util.List;

import com.monocept.myapp.enums.DocumentType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InsuranceSchemeRequestDto {

    private Long schemeId;

    @NotEmpty(message = "Scheme name is required")
    private String schemeName;

    private Long schemeDetailId;

    private boolean active = true;

    @NotEmpty(message = "Detail description is required")
    private String detailDescription;

    @PositiveOrZero(message = "Minimum amount must be a non-negative number")
    private Double minAmount;

    @PositiveOrZero(message = "Maximum amount must be a non-negative number")
    private Double maxAmount;

    @PositiveOrZero(message = "Minimum policy time must be a non-negative number")
    private int minPolicyTerm;

    @PositiveOrZero(message = "Maximum policy time must be a non-negative number")
    private Integer maxPolicyTerm;

    @PositiveOrZero(message = "Minimum age must be a non-negative number")
    private int minAge;

    @PositiveOrZero(message = "Maximum age must be a non-negative number")
    private Integer maxAge;

    @PositiveOrZero(message = "Profit ratio must be a non-negative number")
    private Double profitRatio;

    @PositiveOrZero(message = "Registration commission ratio must be a non-negative number")
    private Double registrationCommRatio;

    @PositiveOrZero(message = "Installment commission ratio must be a non-negative number")
    private Double installmentCommRatio;
    
    @NotEmpty(message = "At least one document type is required")
    private List<DocumentType> requiredDocuments;
}
