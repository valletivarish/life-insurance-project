package com.monocept.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.InsuranceSettingResponseDto;
import com.monocept.myapp.dto.InterestCalculatorRequestDto;
import com.monocept.myapp.dto.InterestCalculatorResponseDto;
import com.monocept.myapp.dto.TaxSettingResponseDto;
import com.monocept.myapp.service.InsuranceManagementService;
import com.monocept.myapp.service.SettingService;

@RestController
@RequestMapping("/GuardianLifeAssurance")
public class GuardianLifeAssuranceController {

	@Autowired
	private SettingService settingService;
	
	@Autowired
	private InsuranceManagementService insuranceManagementService;

	@GetMapping("/insurance-setting")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<InsuranceSettingResponseDto> getInsuranceSetting() {
		return new ResponseEntity<InsuranceSettingResponseDto>(settingService.getInsuranceSetting(), HttpStatus.OK);
	}

	@GetMapping("/tax-setting")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<TaxSettingResponseDto> getTaxSetting() {
		return new ResponseEntity<TaxSettingResponseDto>(settingService.getTaxSetting(), HttpStatus.OK);
	}
	
	@PostMapping("/InterestCalculator")
	public ResponseEntity<InterestCalculatorResponseDto> calculateInterest(
			@RequestBody InterestCalculatorRequestDto interestCalculatorDto) {
		System.out.println(interestCalculatorDto);
		return new ResponseEntity<InterestCalculatorResponseDto>(
				insuranceManagementService.calculateInterest(interestCalculatorDto), HttpStatus.OK);
	}

}
