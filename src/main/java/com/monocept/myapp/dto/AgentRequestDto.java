package com.monocept.myapp.dto;

import lombok.Data;

@Data
public class AgentRequestDto {

	private long agentId;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String username;
	
	private String houseNo;
	private String apartment;
	private int pincode;
	private long stateId;
	private long cityId;
}
