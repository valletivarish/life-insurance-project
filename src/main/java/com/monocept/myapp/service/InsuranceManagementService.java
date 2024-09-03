package com.monocept.myapp.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.InsurancePlanRequestDto;
import com.monocept.myapp.dto.InsurancePlanResponseDto;
import com.monocept.myapp.dto.InsuranceSchemeRequestDto;
import com.monocept.myapp.util.PagedResponse;

public interface InsuranceManagementService {

	String createInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto);

	String updateInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto);

	PagedResponse<InsurancePlanResponseDto> getAllInsurancePlans(int page, int size, String sortBy, String direction);

	String deactivateInsurancePlan(long insuranceTypeId);

	String createInsuranceScheme(long insurancePlanId, MultipartFile multipartFile,
			InsuranceSchemeRequestDto requestDto) throws IOException;

	PagedResponse<InsurancePlanResponseDto> getAllInsuranceSchemes(long insurancePlanId);

}
