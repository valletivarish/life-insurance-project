package com.monocept.myapp.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.InsurancePlanRequestDto;
import com.monocept.myapp.dto.InsurancePlanResponseDto;
import com.monocept.myapp.dto.InsuranceSchemeRequestDto;
import com.monocept.myapp.dto.InsuranceSchemeResponseDto;
import com.monocept.myapp.dto.InterestCalculatorRequestDto;
import com.monocept.myapp.dto.InterestCalculatorResponseDto;
import com.monocept.myapp.util.PagedResponse;

public interface InsuranceManagementService {

	String createInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto);

	String updateInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto);

	PagedResponse<InsurancePlanResponseDto> getAllInsurancePlans(int page, int size, String sortBy, String direction);

	String deactivateInsurancePlan(long insuranceTypeId);

	String createInsuranceScheme(long insurancePlanId, MultipartFile multipartFile,
			InsuranceSchemeRequestDto requestDto) throws IOException;

	List<InsuranceSchemeResponseDto> getAllInsuranceSchemes(long insurancePlanId);

	String updateInsuranceScheme(long insurancePlanId, MultipartFile multipartFile,
			InsuranceSchemeRequestDto requestDto) throws IOException;

	String deleteInsuranceScheme(long insurancePlanId, long insuranceSchemeId);

	InsuranceSchemeResponseDto getInsuranceById(long insurancePlanId, long insuranceSchemeId);

	InterestCalculatorResponseDto calculateInterest(InterestCalculatorRequestDto interestCalculatorDto);



}
