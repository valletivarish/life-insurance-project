package com.monocept.myapp.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustomerRequestDto {
	private long customerId;

	private String firstName;

	private String lastName;

	private LocalDate dateOfBirth;

	private String phoneNumber;
	
	private boolean active;

	private String username;
	private String email;
	private String password;
	private String houseNo;
	private String apartment;
	private int pincode;
	private long stateId;
	private long cityId;
}
