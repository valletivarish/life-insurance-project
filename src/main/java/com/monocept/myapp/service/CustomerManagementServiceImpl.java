package com.monocept.myapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.Employee;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.repository.AddressRepository;
import com.monocept.myapp.repository.CityRepository;
import com.monocept.myapp.repository.CustomerRepository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.StateRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.util.PagedResponse;

@Service
public class CustomerManagementServiceImpl implements CustomerManagementService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public CustomerResponseDto getCustomerIdById(long customerID) {
		Customer customer = customerRepository.findById(customerID).orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.OK, "Customer Not found"));

		return convertCustomerToCustomerResponseDto(customer);
	}

	@Override
	public PagedResponse<CustomerResponseDto> getAllCustomer(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<Customer> customerPage = customerRepository.findAll(pageRequest);
		List<CustomerResponseDto> customers = customerPage.getContent().stream()
				.map(customer -> convertCustomerToCustomerResponseDto(customer)).collect(Collectors.toList());

		return new PagedResponse<>(customers, customerPage.getNumber(), customerPage.getSize(),
				customerPage.getTotalElements(), customerPage.getTotalPages(), customerPage.isLast());
	}
	
	@Override
	public String deactivateCustomer(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.OK, "Customer Not found"));
		if (!customer.isActive()) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.CONFLICT, "Customer is already deleted");
		}
		customer.setActive(false);
		customerRepository.save(customer);
		return "Customer Deleted Successfully";
	}

	private CustomerResponseDto convertCustomerToCustomerResponseDto(Customer customer) {
		CustomerResponseDto customerResponseDto = new CustomerResponseDto();
		customerResponseDto.setUsername(customer.getUser().getUsername());
		customerResponseDto.setUserId(customer.getUser().getUserId());
		customerResponseDto.setCustomerId(customer.getCustomerId());
		customerResponseDto.setFirstName(customer.getFirstName());
		customerResponseDto.setLastName(customer.getLastName());
		customerResponseDto.setEmail(customer.getUser().getEmail());
		customerResponseDto.setActive(customer.isActive());

		return customerResponseDto;
	}

}
