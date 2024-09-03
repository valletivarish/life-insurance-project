package com.monocept.myapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.TaxSettingRequestDto;
import com.monocept.myapp.entity.TaxSetting;
import com.monocept.myapp.repository.TaxSettingRepository;

@Service
public class SettingServiceImpl implements SettingService {
//	
//	@Autowired
//	private StateRepository stateRepository;
	@Autowired
	private TaxSettingRepository taxSettingRepository;
	@Override
	public String createTaxSetting(TaxSettingRequestDto taxSettingRequestDto) {
		TaxSetting taxSetting=new TaxSetting();
		taxSetting.setTaxPercentage(taxSettingRequestDto.getTaxPercentage());
		taxSetting.setUpdatedAt(taxSettingRequestDto.getUpdatedAt());
		taxSettingRepository.save(taxSetting);
		return "Tax Setting updated";
	}
	

//	@Override
//	public String createTaxSetting(TaxSettingRequestDto taxSettingRequestDto) {
//		TaxSetting taxSetting=new TaxSetting();
//		taxSetting.setTaxPercentage(taxSettingRequestDto.getTaxPercentage());
//		State state = stateRepository.findById(taxSettingRequestDto.getStateId()).orElseThrow(()->new StudentApiException(HttpStatus.NOT_FOUND, "State not found"));
//		String description="State "+state.getName()+" tax percentage has been updated with "+taxSettingRequestDto.getTaxPercentage();
//		taxSetting.setDescription(description);
//		taxSetting.setState(state);
//		taxSetting.setUpdatedAt(taxSettingRequestDto.getUpdatedAt());
//		taxSettingRepository.save(taxSetting);
//		return "Tax setting updated";
//	}


	

}
