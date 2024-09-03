package com.monocept.myapp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.entity.Address;
import com.monocept.myapp.entity.City;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.State;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.repository.AddressRepository;
import com.monocept.myapp.repository.CityRepository;
import com.monocept.myapp.repository.CustomerRepository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.StateRepository;
import com.monocept.myapp.repository.UserRepository;

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
	public String createCustomer(CustomerRequestDto customerRequestDto) {
		Customer customer=new Customer();
		convertCustomerRequestDtoToCustomer(customerRequestDto,customer);
		return "Customer Created Successfully";
	}
	private Customer convertCustomerRequestDtoToCustomer(CustomerRequestDto customerRequestDto, Customer customer) {
		if (userRepository.existsByUsername(customerRequestDto.getUsername())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Username already exists!");
		}

		if (userRepository.existsByEmail(customerRequestDto.getEmail())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Email already exists!");
		}
		User user = new User();
		user.setEmail(customerRequestDto.getEmail());
		user.setUsername(customerRequestDto.getUsername());
		user.setPassword(passwordEncoder.encode(customerRequestDto.getPassword()));
		Set<Role> roles = new HashSet<>();
		String roleName="ROLE_CUSTOMER";
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));
		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);
		Address address = new Address();
		State state = stateRepository.findById(customerRequestDto.getStateId()).orElse(null);
		List<City> cities = state.getCity();
		City city = cities.stream().filter(c -> c.getCityId() == customerRequestDto.getCityId()).findFirst().orElse(null);
		address.setCity(city);
		address.setHouseNo(customerRequestDto.getHouseNo());
		address.setPincode(customerRequestDto.getPincode());
		address.setApartment(customerRequestDto.getApartment());

		addressRepository.save(address);
		customer.setActive(customerRequestDto.isActive());
		customer.setAddress(address);
		customer.setDateOfBirth(customerRequestDto.getDateOfBirth());
		customer.setFirstName(customerRequestDto.getFirstName());
		customer.setLastName(customerRequestDto.getLastName());
		customer.setPhoneNumber(customer.getPhoneNumber());
		customer.setUser(user);
		return customerRepository.save(customer);
	}
	@Override
	public String updateCustomer(CustomerRequestDto customerRequestDto) {
		Customer customer = customerRepository.findById(customerRequestDto.getCustomerId()).orElseThrow(()->new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Customer Not found"));
		convertCustomerRequestDtoToCustomer(customerRequestDto, customer);
		return "Customer Updated Successfully";
	}
	
	
	

}
