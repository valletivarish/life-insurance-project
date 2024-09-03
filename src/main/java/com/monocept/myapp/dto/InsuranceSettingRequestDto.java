package com.monocept.myapp.dto;

import lombok.Data;

@Data
public class InsuranceSettingRequestDto {
    private double claimDeduction;

    private double penaltyAmount;
}
