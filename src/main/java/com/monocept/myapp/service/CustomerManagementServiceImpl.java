package com.monocept.myapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
