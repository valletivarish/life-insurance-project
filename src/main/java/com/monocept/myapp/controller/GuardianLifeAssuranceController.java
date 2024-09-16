package com.monocept.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.InsurancePlanResponseDto;
import com.monocept.myapp.dto.InsuranceSettingResponseDto;
import com.monocept.myapp.dto.InterestCalculatorRequestDto;
import com.monocept.myapp.dto.InterestCalculatorResponseDto;
import com.monocept.myapp.dto.StateResponseDto;
import com.monocept.myapp.dto.TaxSettingResponseDto;
import com.monocept.myapp.service.InsuranceManagementService;
import com.monocept.myapp.service.SettingService;
import com.monocept.myapp.service.StateAndCityManagementService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/GuardianLifeAssurance")
public class GuardianLifeAssuranceController {

	@Autowired
	private SettingService settingService;
	
	@Autowired
	private InsuranceManagementService insuranceManagementService;
	
	@Autowired
	private StateAndCityManagementService stateAndCityManagementService;

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
	@GetMapping("/insurance-plans")
	@Operation(summary = "Get all insurance plans", description = "Retrieve a paginated list of all insurance plans")
	public ResponseEntity<PagedResponse<InsurancePlanResponseDto>> getAllInsurancePlans(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "planId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<>(insuranceManagementService.getAllInsurancePlans(page, size, sortBy, direction),
				HttpStatus.OK);
	}
	@GetMapping("states")
	@Operation(summary = "Get all states", description = "Retrieve a paginated list of all states")
	public ResponseEntity<PagedResponse<StateResponseDto>> getAllStates(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "stateId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<StateResponseDto>>(
				stateAndCityManagementService.getAllStates(page, size, sortBy, direction), HttpStatus.OK);

	}
	@GetMapping("states/count")
	public ResponseEntity<Long> getStateCount(){
		return new ResponseEntity<Long>(stateAndCityManagementService.getCount(),HttpStatus.OK);
	}
	@GetMapping("/insurance-plans/count")
	public ResponseEntity<Long> getPlanCount() {
		return new ResponseEntity<Long>(insuranceManagementService.getPlanCount(),HttpStatus.OK);
	}
	

}
