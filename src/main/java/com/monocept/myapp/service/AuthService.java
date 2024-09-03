package com.monocept.myapp.service;

import com.monocept.myapp.dto.LoginDto;
import com.monocept.myapp.dto.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
