package com.monocept.myapp.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.InsurancePlanRequestDto;
import com.monocept.myapp.dto.InsurancePlanResponseDto;
import com.monocept.myapp.dto.InsuranceSchemeRequestDto;
import com.monocept.myapp.dto.InsuranceSchemeResponseDto;
import com.monocept.myapp.entity.InsurancePlan;
import com.monocept.myapp.entity.InsuranceScheme;
import com.monocept.myapp.entity.SchemeDetail;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.repository.InsurancePlanRepository;
import com.monocept.myapp.repository.InsuranceSchemeRepository;
import com.monocept.myapp.repository.SchemeDetailRepository;
import com.monocept.myapp.util.ImageUtil;
import com.monocept.myapp.util.PagedResponse;

@Service
public class InsuranceManagementServiceImpl implements InsuranceManagementService {

	@Autowired
	private InsurancePlanRepository insurancePlanRepository;

	@Autowired
	private SchemeDetailRepository schemeDetailRepository;

	@Autowired
	private InsuranceSchemeRepository insuranceSchemeRepository;

	@Override
	public String createInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto) {
		InsurancePlan insurancePlan = new InsurancePlan();
		insurancePlan.setPlanId(insurancePlanRequestDto.getPlanId());
		insurancePlan.setPlanName(insurancePlanRequestDto.getPlanName());
		insurancePlan.setActive(insurancePlanRequestDto.isActive());
		insurancePlanRepository.save(insurancePlan);
		return "Insurance Plan Created Successfully";
	}

	@Override
	public String updateInsurancePlan(InsurancePlanRequestDto insurancePlanRequestDto) {
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insurancePlanRequestDto.getPlanId())
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Plan "));
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
	public String deactivateInsurancePlan(long insuranceTypeId) {
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insuranceTypeId)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Plan "));
		insurancePlan.setActive(false);
		insurancePlanRepository.save(insurancePlan);
		return "Insurance Plan deleted Successfully";
	}

	@Override
	public String createInsuranceScheme(long insurancePlanId, MultipartFile multipartFile,
			InsuranceSchemeRequestDto requestDto) throws IOException {
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insurancePlanId).orElseThrow(
				() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance plan not found"));
		InsuranceScheme insuranceScheme = new InsuranceScheme();
		SchemeDetail schemeDetail = new SchemeDetail();
		schemeDetail.setSchemeImage(ImageUtil.compressFile(multipartFile.getBytes()));
		schemeDetail.setDescription(requestDto.getDetailDescription());
		schemeDetail.setInstallmentCommRatio(requestDto.getInstallmentCommRatio());
		schemeDetail.setMaxAge(requestDto.getMaxAge());
		schemeDetail.setMaxAmount(requestDto.getMaxAmount());
		schemeDetail.setMaxPolicyTerm(requestDto.getMaxPolicyTerm());
		schemeDetail.setMinAge(requestDto.getMinAge());
		schemeDetail.setMinAmount(requestDto.getMinAmount());
		schemeDetail.setMinPolicyTerm(requestDto.getMinPolicyTerm());
		schemeDetail.setProfitRatio(requestDto.getProfitRatio());
		schemeDetail.setRegistrationCommRatio(requestDto.getRegistrationCommRatio());
		schemeDetailRepository.save(schemeDetail);
		insuranceScheme.setActive(true);
		insuranceScheme.setDescription(requestDto.getDescription());
		insuranceScheme.setSchemeName(requestDto.getSchemeName());
		insuranceScheme.setSchemeDetail(schemeDetail);
		insuranceSchemeRepository.save(insuranceScheme);
		List<InsuranceScheme> schemes = insurancePlan.getScheme();
		schemes.add(insuranceScheme);
		insurancePlan.setScheme(schemes);
		insurancePlanRepository.save(insurancePlan);

		return "Insurance Scheme Added Successfully";
	}

	@Override
	public List<InsuranceSchemeResponseDto> getAllInsuranceSchemes(long insurancePlanId) {
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insurancePlanId).orElseThrow(
				() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Plan not found"));
		List<InsuranceSchemeResponseDto> schemes = insurancePlan.getScheme().stream()
				.map(scheme -> convertSchemeToSchemeResponseDto(scheme)).collect(Collectors.toList());
		return schemes;
	}

	private InsuranceSchemeResponseDto convertSchemeToSchemeResponseDto(InsuranceScheme scheme) {
		InsuranceSchemeResponseDto schemeResponseDto = new InsuranceSchemeResponseDto();
		schemeResponseDto.setActive(scheme.isActive());
		schemeResponseDto.setDescription(scheme.getDescription());
		SchemeDetail schemeDetail = scheme.getSchemeDetail();
		schemeResponseDto.setSchemeDetailId(schemeDetail.getDetailId());
		schemeResponseDto.setSchemeId(scheme.getSchemeId());
		schemeResponseDto.setDetailDescription(schemeDetail.getDescription());
		schemeResponseDto.setInstallmentCommRatio(schemeDetail.getInstallmentCommRatio());
		schemeResponseDto.setMaxAge(schemeDetail.getMaxAge());
		schemeResponseDto.setMaxAmount(schemeDetail.getMaxAmount());
		schemeResponseDto.setMaxPolicyTerm(schemeDetail.getMaxPolicyTerm());
		schemeResponseDto.setMinAge(schemeDetail.getMinAge());
		schemeResponseDto.setMinAmount(schemeDetail.getMinAmount());
		schemeResponseDto.setMinPolicyTerm(schemeDetail.getMinPolicyTerm());
		schemeResponseDto.setProfitRatio(schemeDetail.getProfitRatio());
		schemeResponseDto.setRegistrationCommRatio(schemeDetail.getRegistrationCommRatio());
		schemeResponseDto.setSchemeName(scheme.getSchemeName());
		return schemeResponseDto;
	}

	@Override
	public String updateInsuranceScheme(long insurancePlanId, MultipartFile multipartFile,
			InsuranceSchemeRequestDto requestDto) throws IOException {
		insurancePlanRepository.findById(insurancePlanId).orElseThrow(
				() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance plan not found"));
		InsuranceScheme insuranceScheme = insuranceSchemeRepository.findById(requestDto.getSchemeId()).orElseThrow(
				() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Scheme not found"));
		SchemeDetail schemeDetail = insuranceScheme.getSchemeDetail();
		schemeDetail.setDescription(requestDto.getDetailDescription());
		schemeDetail.setInstallmentCommRatio(requestDto.getInstallmentCommRatio());
		schemeDetail.setMaxAge(requestDto.getMaxAge());
		schemeDetail.setMaxAmount(requestDto.getMaxAmount());
		schemeDetail.setMaxPolicyTerm(requestDto.getMaxPolicyTerm());
		schemeDetail.setMinAge(requestDto.getMinAge());
		schemeDetail.setMinAmount(requestDto.getMinAmount());
		schemeDetail.setMinPolicyTerm(requestDto.getMinPolicyTerm());
		schemeDetail.setProfitRatio(requestDto.getProfitRatio());
		schemeDetail.setRegistrationCommRatio(requestDto.getRegistrationCommRatio());
		schemeDetail.setSchemeImage(ImageUtil.compressFile(multipartFile.getBytes()));
		insuranceScheme.setSchemeDetail(schemeDetail);
		insuranceScheme.setActive(requestDto.isActive());
		insuranceScheme.setDescription(requestDto.getDescription());
		insuranceScheme.setSchemeName(requestDto.getSchemeName());
		insuranceSchemeRepository.save(insuranceScheme);

		return "Insurance Scheme updated Successfully";
	}

	@Override
	public String deleteInsuranceScheme(long insurancePlanId, long insuranceSchemeId) {
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insurancePlanId).orElseThrow(()->new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Plan not found"));
		InsuranceScheme schemeToDelete = insurancePlan.getScheme().stream()
		        .filter(scheme -> scheme.getSchemeId().equals(insuranceSchemeId))
		        .findFirst()
		        .orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Scheme not found"));
		schemeToDelete.setActive(false);
		insuranceSchemeRepository.save(schemeToDelete);
		return "Insurance Scheme Deleted Successfully";
	}

	@Override
	public InsuranceSchemeResponseDto getInsuranceById(long insurancePlanId, long insuranceSchemeId) {
		InsurancePlan insurancePlan = insurancePlanRepository.findById(insurancePlanId).orElseThrow(()->new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Plan not found"));
		InsuranceScheme schemeById = insurancePlan.getScheme().stream()
		        .filter(scheme -> scheme.getSchemeId().equals(insuranceSchemeId))
		        .findFirst()
		        .orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Insurance Scheme not found"));
		return convertSchemeToSchemeResponseDto(schemeById);
	}

}
