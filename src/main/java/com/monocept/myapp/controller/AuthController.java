package com.monocept.myapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.ForgetPasswordRequestDto;
import com.monocept.myapp.dto.JwtResponse;
import com.monocept.myapp.dto.LoginDto;
import com.monocept.myapp.dto.RegisterDto;
import com.monocept.myapp.dto.ResetPasswordRequestDto;
import com.monocept.myapp.security.JwtTokenProvider;
import com.monocept.myapp.service.AuthService;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.EmailService;
import com.monocept.myapp.service.StripeService;
import com.stripe.model.Balance;

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
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@PostMapping("/customer-registration")
	public ResponseEntity<String> createCustomer(@RequestBody CustomerRequestDto customerRequestDto) {
		return new ResponseEntity<String>(customerManagementService.createCustomer(customerRequestDto),
				HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDto loginRequest) {
		JwtResponse jwtResponse = authService.login(loginRequest);

		String token = jwtTokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());

		return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(jwtResponse);
	}

	@PostMapping(value = { "/register" })
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

	@Autowired
	private StripeService service;

	@GetMapping("/balance")
	public ResponseEntity<String> retrieveBalance() {
		Balance balance = service.retrieveBalance();
		return ResponseEntity.ok(balance.toJson());
	}
}
