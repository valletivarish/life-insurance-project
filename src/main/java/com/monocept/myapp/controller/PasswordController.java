package com.monocept.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.OtpForgetPasswordRequest;
import com.monocept.myapp.service.EmailService;

@RestController
@RequestMapping("/GuardianLifeAssurance/password")
public class PasswordController {
	@Autowired
	private EmailService emailService;

	@PostMapping("/reset-password")
	public ResponseEntity<String> verifyOtpAndSetNewPassword(
			@RequestBody @Validated OtpForgetPasswordRequest forgetPasswordRequest) {
		String response = emailService.verifyOtpAndSetNewPassword(forgetPasswordRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
