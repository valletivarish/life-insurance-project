package com.monocept.myapp.dto;

import lombok.Data;

@Data
public class AgentResponseDto {
	private long agentId;
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	
	private String houseNo;
	private String apartment;
	private int pincode;
	private String state;
	private String city;
}
