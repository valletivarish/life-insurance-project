package com.monocept.myapp.dto;

import lombok.Data;

@Data
public class InsuranceSchemeResponseDto {

    private long schemeId;

    private String schemeName;

    private String description;

    private long schemeDetailId;

    private boolean active;

    private String detailDescription;
    private Double minAmount;
    private Double maxAmount;
    private int minPolicyTerm;
    private Integer maxPolicyTerm;
    private int minAge;
    private Integer maxAge;
    private Double profitRatio;
    private Double registrationCommRatio;
    private Double installmentCommRatio;


}
