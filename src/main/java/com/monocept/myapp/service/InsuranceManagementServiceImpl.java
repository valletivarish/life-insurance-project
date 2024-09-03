package com.monocept.myapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.monocept.myapp.dto.InsurancePlanRequestDto;
import com.monocept.myapp.dto.InsurancePlanResponseDto;
import com.monocept.myapp.entity.InsurancePlan;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
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
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insurancePlanRequestDto.getPlanId()).orElseThrow(()->new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Plan "));
		insurancePlan.setPlanName(insurancePlanRequestDto.getPlanName());
		insurancePlan.setActive(insurancePlanRequestDto.isActive());
		insurancePlanRepository.save(insurancePlan);
		return "Insurance Plan Updated Successfully";
	}

	@Override
	public PagedResponse<InsurancePlanResponseDto> getAllInsurancePlans(int page, int size, String sortBy,
			String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<InsurancePlan> insurancePlanPage = insurancePlanRepository.findAll(pageRequest);

		List<InsurancePlanResponseDto> list = insurancePlanPage.getContent().stream()
				.map(insuranceType -> convertInsuranceToInsuranceResponseDto(insuranceType))
				.collect(Collectors.toList());
		return new PagedResponse<>(list, insurancePlanPage.getNumber(), insurancePlanPage.getSize(),
				insurancePlanPage.getTotalElements(), insurancePlanPage.getTotalPages(), insurancePlanPage.isLast());
	}
	
	private InsurancePlanResponseDto convertInsuranceToInsuranceResponseDto(InsurancePlan insurancePlan) {
		InsurancePlanResponseDto insuranceResponseDto = new InsurancePlanResponseDto();
		insuranceResponseDto.setPlanId(insurancePlan.getPlanId());
		insuranceResponseDto.setPlanName(insurancePlan.getPlanName());
		insuranceResponseDto.setActive(insurancePlan.isActive());
		return insuranceResponseDto;
	}

	@Override
	public String deactivateInsurationPlan(long insuranceTypeId) {
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insuranceTypeId).orElseThrow(()->new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Plan "));
		insurancePlan.setActive(false);
		insurancePlanRepository.save(insurancePlan);
		return "Insurance Plan deleted Successfully";
	}

}
