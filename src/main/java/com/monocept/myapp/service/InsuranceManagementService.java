package com.monocept.myapp.service;

import com.monocept.myapp.dto.InsurancePlanRequestDto;
import com.monocept.myapp.dto.InsurancePlanResponseDto;
import com.monocept.myapp.util.PagedResponse;

public interface InsuranceManagementService {

	String createInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto);

	String updateInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto);

	PagedResponse<InsurancePlanResponseDto> getAllInsurancePlans(int page, int size, String sortBy, String direction);

	String deactivateInsurationPlan(long insuranceTypeId);

}
