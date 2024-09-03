package com.monocept.myapp.dto;

import lombok.Data;

@Data
public class QueryResponseDto {
	private long customerId;
	private String customerName;
	private String title;
	private String message;
	private String response;
	private boolean resolved;
}
