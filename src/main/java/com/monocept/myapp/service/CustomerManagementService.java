package com.monocept.myapp.service;

import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.util.PagedResponse;

public interface CustomerManagementService {

	CustomerResponseDto getCustomerIdById(long customerID);

	PagedResponse<CustomerResponseDto> getAllCustomer(int page, int size, String sortBy, String direction);

	String deactivateCustomer(Long customerID);

}
