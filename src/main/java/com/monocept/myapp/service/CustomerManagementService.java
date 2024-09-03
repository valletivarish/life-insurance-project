package com.monocept.myapp.service;

import com.monocept.myapp.dto.CustomerRequestDto;

public interface CustomerManagementService {

	String createCustomer(CustomerRequestDto customerRequestDto);

	String updateCustomer(CustomerRequestDto customerRequestDto);


}
