package com.monocept.myapp.service;

import com.monocept.myapp.dto.ChangePasswordRequestDto;
import com.monocept.myapp.dto.JwtResponse;
import com.monocept.myapp.dto.LoginDto;
import com.monocept.myapp.dto.RegisterDto;

public interface AuthService {
    JwtResponse login(LoginDto loginDto);

    String register(RegisterDto registerDto);

	String changePassword(ChangePasswordRequestDto changePasswordRequestDto);
}
