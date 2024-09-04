package com.monocept.myapp.service;

import com.monocept.myapp.dto.AdminRequestDto;
import com.monocept.myapp.dto.RegisterDto;

import jakarta.validation.Valid;

public interface AdminService {

	

	String updateAdmin(@Valid AdminRequestDto adminRequestDto);

	String deleteAdmin(long adminId);

	

}
