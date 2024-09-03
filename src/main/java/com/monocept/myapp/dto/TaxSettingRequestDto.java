package com.monocept.myapp.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TaxSettingRequestDto {
    private double taxPercentage;

    private LocalDateTime updatedAt=LocalDateTime.now();
}
