package com.monocept.myapp.service;

import com.monocept.myapp.dto.OtpForgetPasswordRequest;

public interface EmailService {

	String sendOtpForForgetPassword(String usernameOrEmail);

	String verifyOtpAndSetNewPassword(OtpForgetPasswordRequest forgetPasswordRequest);

}
