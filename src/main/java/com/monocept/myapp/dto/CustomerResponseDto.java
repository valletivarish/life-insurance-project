package com.monocept.myapp.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustomerResponseDto {
	private String firstName;

	private String lastName;

	private LocalDate dateOfBirth;

	private String phoneNumber;
	

	private String username;
	private String email;
	private String password;
	private String houseNo;
	private String apartment;
	private int pincode;
	private String state;
	private String city;
}
