package com.monocept.myapp.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class CommissionResponseDto {
    private long commissionId;
    private String commissionType; 
    private Date issueDate;
    private double amount;
}