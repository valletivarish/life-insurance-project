package com.monocept.myapp.dto;

import lombok.Data;

@Data
public class AdminRequestDto {


	    private long adminId;

	   
	    private String username;

	   
	    private String email;

	   
	    private String password;
	   

	    private boolean active;
	

}
