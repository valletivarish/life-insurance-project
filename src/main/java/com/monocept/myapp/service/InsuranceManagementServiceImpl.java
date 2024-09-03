package com.monocept.myapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.monocept.myapp.dto.InsurancePlanRequestDto;
import com.monocept.myapp.dto.InsurancePlanResponseDto;
import com.monocept.myapp.entity.InsurancePlan;
import com.monocept.myapp.exception.StudentApiException;
import com.monocept.myapp.repository.InsurancePlanRepository;
import com.monocept.myapp.util.PagedResponse;

public class InsuranceManagementServiceImpl implements InsuranceManagementService{
	
	@Autowired
	private InsurancePlanRepository insurancePlanRepository;

	@Override
	public String createInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto) {
		InsurancePlan insurancePlan=new InsurancePlan();
		insurancePlan.setPlanId(insurancePlanRequestDto.getPlanId());
		insurancePlan.setPlanName(insurancePlanRequestDto.getPlanName());
		insurancePlan.setActive(insurancePlanRequestDto.isActive());
		insurancePlanRepository.save(insurancePlan);
		return "Insurance Plan Created Successfully";
	}

	@Override
	public String updateInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto) {
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insurancePlanRequestDto.getPlanId()).orElseThrow(()->new StudentApiException(HttpStatus.NOT_FOUND, "Insurance Plan "));
		insurancePlan.setPlanName(insurancePlanRequestDto.getPlanName());
		insurancePlan.setActive(insurancePlanRequestDto.isActive());
		insurancePlanRepository.save(insurancePlan);
		return "Insurance Plan Updated Successfully";
	}

	@Override
	public PagedResponse<InsurancePlanResponseDto> getAllInsurancePlans(int page, int size, String sortBy,
			String direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deactivateInsurationPlan(long insuranceTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

}
