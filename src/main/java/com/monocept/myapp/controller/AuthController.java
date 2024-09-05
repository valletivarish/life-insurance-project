package com.monocept.myapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.ForgetPasswordRequestDto;
import com.monocept.myapp.dto.JWTAuthResponse;
import com.monocept.myapp.dto.LoginDto;
import com.monocept.myapp.dto.RegisterDto;
import com.monocept.myapp.dto.ResetPasswordRequestDto;
import com.monocept.myapp.service.AuthService;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.EmailService;

@RestController
@RequestMapping("/GuardianLifeAssurance/auth")
public class AuthController {

	private AuthService authService;
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Autowired
	private CustomerManagementService customerManagementService;

	@Autowired
	private EmailService emailService;

	@PostMapping("/customer-registration")
	public ResponseEntity<String> createCustomer(@RequestBody CustomerRequestDto customerRequestDto) {
		return new ResponseEntity<String>(customerManagementService.createCustomer(customerRequestDto),
				HttpStatus.CREATED);
	}

	// Build Login REST API
	@PostMapping(value = { "/login", "/signin" })
	public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto) {
		logger.trace("A TRACE Message");
		logger.debug("A DEBUG Message");
		logger.info("An INFO Message");
		logger.warn("A WARN Message");
		logger.error("An ERROR Message");
		String token = authService.login(loginDto);
		System.out.println(loginDto);
		JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
		jwtAuthResponse.setAccessToken(token);

		return ResponseEntity.ok(jwtAuthResponse);
	}

	// Build Register REST API
	@PostMapping(value = { "/register", "/signup" })
	public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {

		logger.trace("A TRACE Message" + registerDto);
		logger.debug("A DEBUG Message");
		logger.info("An INFO Message");
		logger.warn("A WARN Message");
		logger.error("An ERROR Message");
		String response = authService.register(registerDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/send-otp")
	public ResponseEntity<String> sendOtpForForgetPassword(
			@RequestBody ForgetPasswordRequestDto otpForgetPasswordRequest) {
		String response = emailService.sendOtpForForgetPassword(otpForgetPasswordRequest.getUsernameOrEmail());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtpAndSetNewPassword(
			@RequestBody @Validated ResetPasswordRequestDto forgetPasswordRequest) {
		String response = emailService.verifyOtpAndSetNewPassword(forgetPasswordRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
