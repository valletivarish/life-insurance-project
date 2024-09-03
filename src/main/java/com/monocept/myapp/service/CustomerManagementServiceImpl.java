package com.monocept.myapp.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.CustomerSideQueryRequestDto;
import com.monocept.myapp.dto.QueryReplyDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.entity.Address;
import com.monocept.myapp.entity.City;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.Document;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.Query;
import com.monocept.myapp.entity.State;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.repository.AddressRepository;
import com.monocept.myapp.repository.CustomerRepository;
import com.monocept.myapp.repository.DocumentRepository;
import com.monocept.myapp.repository.QueryRepository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.StateRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.util.ImageUtil;
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
	private QueryRepository queryRepository;
//
//	@Autowired
//	private CityRepository cityRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public String createCustomer(CustomerRequestDto customerRequestDto) {
		Customer customer = new Customer();
		convertCustomerRequestDtoToCustomer(customerRequestDto, customer);
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
		String roleName = "ROLE_CUSTOMER";
		Role role = roleRepository.findByName(roleName).orElseThrow(
				() -> new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));
		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);
		Address address = new Address();
		State state = stateRepository.findById(customerRequestDto.getStateId()).orElse(null);
		List<City> cities = state.getCity();
		City city = cities.stream().filter(c -> c.getCityId() == customerRequestDto.getCityId()).findFirst()
				.orElse(null);
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
		customer.setPhoneNumber(customerRequestDto.getPhoneNumber());
		customer.setUser(user);
		return customerRepository.save(customer);
	}

	@Override
	public String updateCustomer(CustomerRequestDto customerRequestDto) {
		Customer customer = customerRepository.findById(customerRequestDto.getCustomerId())
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Customer Not found"));
		convertCustomerRequestDtoToCustomer(customerRequestDto, customer);
		return "Customer Updated Successfully";
	}

	@Override
	public CustomerResponseDto getCustomerIdById(long customerID) {
		Customer customer = customerRepository.findById(customerID)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.OK, "Customer Not found"));

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

	@Override
	public String uploadDocument(MultipartFile file, String documentName, long customerId) throws IOException {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Customer not found"));

		Document document = new Document();
		document.setCustomer(customer);
		document.setContent(ImageUtil.compressFile(file.getBytes()));
		document.setDocumentName(documentName);
		documentRepository.save(document);
		return "Document uploaded successfully";
	}

	@Override
	public String createCustomerQuery(long customerId, CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Customer Not found"));
		Query query = new Query();
		query.setCustomer(customer);
		query.setMessage(customerSideQueryRequestDto.getMessage());
		query.setTitle(customerSideQueryRequestDto.getTitle());
		queryRepository.save(query);
		return "Query Submitted Successfully";
	}

	@Override
	public String updateCustomerQuery(long customerId, CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Customer Not found"));
		Query existingQuery = queryRepository.findById(customerSideQueryRequestDto.getId())
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Query not found"));
		existingQuery.setMessage(customerSideQueryRequestDto.getMessage());
		existingQuery.setTitle(customerSideQueryRequestDto.getTitle());
		queryRepository.save(existingQuery);
		return "Query Updated Successfully";
	}

	@Override
	public PagedResponse<QueryResponseDto> getAllQueriesByCustomer(long customerId, int page, int size, String sortBy,
			String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Customer customer = customerRepository.findById(customerId).orElseThrow();
		Page<Query> queryPage = queryRepository.findByCustomer(customer, pageRequest);
		List<QueryResponseDto> queries = queryPage.getContent().stream()
				.map(query -> convertQueryToQueryResponseDto(query)).collect(Collectors.toList());
		return new PagedResponse<QueryResponseDto>(queries, queryPage.getNumber(), queryPage.getSize(),
				queryPage.getTotalElements(), queryPage.getTotalPages(), queryPage.isLast());
	}

	private QueryResponseDto convertQueryToQueryResponseDto(Query query) {
		QueryResponseDto queryResponseDto = new QueryResponseDto();
		queryResponseDto.setCustomerId(query.getQueryId());
		queryResponseDto.setCustomerName(query.getCustomer().getFirstName() + " " + query.getCustomer().getLastName());
		queryResponseDto.setMessage(query.getMessage());
		queryResponseDto.setResolved(query.isResolved());
		queryResponseDto.setTitle(query.getTitle());
		queryResponseDto.setResponse(query.getResponse());
		return queryResponseDto;
	}

	@Override
	public String deleteQuery(long customerId, long queryId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Customer Not found"));
		boolean removed = customer.getQueries().removeIf(query -> query.getQueryId() == queryId);
		if (!removed) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Query Not found");
		}
		customerRepository.save(customer);
		return "Query deleted successfully";
	}

	@Override
	public String respondToQuery(long queryId,QueryReplyDto queryReplyDto) {
		Query query = queryRepository.findById(queryId).orElseThrow(()->new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Query Not found"));
		query.setResolved(true);
		query.setResponse(queryReplyDto.getResponse());
		return "Response to query ID " + queryId + " has been successfully recorded.";
	}

	@Override
	public PagedResponse<QueryResponseDto> getAllQueries(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<Query> queryPage = queryRepository.findAllByResolvedFalse(pageRequest);
		List<QueryResponseDto> queryDtos = queryPage.getContent().stream()
		        .map(query -> convertQueryToQueryResponseDto(query))
		        .collect(Collectors.toList());
		 return new PagedResponse<>(queryDtos, queryPage.getNumber(), queryPage.getSize(),
                 queryPage.getTotalElements(), queryPage.getTotalPages(), queryPage.isLast());
	}

}
