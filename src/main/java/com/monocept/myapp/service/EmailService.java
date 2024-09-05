package com.monocept.myapp.service;

import com.monocept.myapp.dto.ResetPasswordRequestDto;

public interface EmailService {

	String sendOtpForForgetPassword(String userNameOrEmail);

	String verifyOtpAndSetNewPassword(ResetPasswordRequestDto forgetPasswordRequest);

}
