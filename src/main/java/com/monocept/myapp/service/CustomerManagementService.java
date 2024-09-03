package com.monocept.myapp.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.CustomerSideQueryRequestDto;
import com.monocept.myapp.dto.QueryReplyDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.util.PagedResponse;

public interface CustomerManagementService {

	String updateCustomer(CustomerRequestDto customerRequestDto);

	String createCustomer(CustomerRequestDto customerRequestDto);

	CustomerResponseDto getCustomerIdById(long customerID);

	PagedResponse<CustomerResponseDto> getAllCustomer(int page, int size, String sortBy, String direction);

	String deactivateCustomer(Long customerID);

	String uploadDocument(MultipartFile file, String documentName, long customerId) throws IOException;

	String createCustomerQuery(long customerId, CustomerSideQueryRequestDto customerSideQueryRequestDto);

	String updateCustomerQuery(long customerId, CustomerSideQueryRequestDto customerSideQueryRequestDto);

	PagedResponse<QueryResponseDto> getAllQueriesByCustomer(long customerId, int page, int size, String sortBy, String direction);

	String deleteQuery(long customerId, long queryId);

	String respondToQuery(long queryId, QueryReplyDto queryReplyDto);

	PagedResponse<QueryResponseDto> getAllQueries(int page, int size, String sortBy, String direction);

}
