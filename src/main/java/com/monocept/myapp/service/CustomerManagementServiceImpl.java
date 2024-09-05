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
import com.monocept.myapp.dto.PolicyAccountRequestDto;
import com.monocept.myapp.dto.PolicyAccountResponseDto;
import com.monocept.myapp.dto.QueryReplyDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.entity.Address;
import com.monocept.myapp.entity.Agent;
import com.monocept.myapp.entity.City;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.Document;
import com.monocept.myapp.entity.InsuranceScheme;
import com.monocept.myapp.entity.PolicyAccount;
import com.monocept.myapp.entity.Query;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.State;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.enums.DocumentType;
import com.monocept.myapp.enums.PremiumType;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException;
import com.monocept.myapp.repository.AddressRepository;
import com.monocept.myapp.repository.AgentRepository;
import com.monocept.myapp.repository.CustomerRepository;
import com.monocept.myapp.repository.DocumentRepository;
import com.monocept.myapp.repository.InsuranceSchemeRepository;
import com.monocept.myapp.repository.PolicyRepository;
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

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AgentRepository agentRepository;

	@Autowired
	private InsuranceSchemeRepository insuranceSchemeRepository;

	@Autowired
	private PolicyRepository policyRepository;

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
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerRequestDto.getCustomerId()));
		convertCustomerRequestDtoToCustomer(customerRequestDto, customer);
		customerRepository.save(customer);
		return "The Customer with ID " + customerRequestDto.getCustomerId() + " has been successfully updated.";
	}

	@Override
	public CustomerResponseDto getCustomerIdById(long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));

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
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		if (!customer.isActive()) {
			throw new GuardianLifeAssuranceException.UserAlreadyDeActivatedException(
					"The customer with ID " + customerId + " is already deactivated.");
		}
		customer.setActive(false);
		customerRepository.save(customer);
		return "The customer with ID " + customerId + " has been successfully deactivated.";
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
	public String uploadDocument(MultipartFile file, DocumentType documentName, long customerId) throws IOException {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));

		Document document = new Document();
		document.setCustomer(customer);
		document.setContent(ImageUtil.compressFile(file.getBytes()));
		document.setDocumentName(documentName);
		documentRepository.save(document);
		return "Document '" + documentName + "' has been successfully uploaded for customer ID " + customerId + ".";
	}

	@Override
	public String createCustomerQuery(long customerId, CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		Query query = new Query();
		query.setCustomer(customer);
		query.setMessage(customerSideQueryRequestDto.getMessage());
		query.setTitle(customerSideQueryRequestDto.getTitle());
		queryRepository.save(query);
		return "Your query titled '" + customerSideQueryRequestDto.getTitle() + "' has been successfully submitted.";
	}

	@Override
	public String updateCustomerQuery(long customerId, CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		Query existingQuery = queryRepository.findById(customerSideQueryRequestDto.getId())
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Sorry, we couldn't find a query with ID: " + customerSideQueryRequestDto.getId()));
		existingQuery.setMessage(customerSideQueryRequestDto.getMessage());
		existingQuery.setTitle(customerSideQueryRequestDto.getTitle());
		queryRepository.save(existingQuery);
		return "Your query with ID " + customerSideQueryRequestDto.getId() + " has been successfully updated.";
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
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		boolean removed = customer.getQueries().removeIf(query -> query.getQueryId() == queryId);
		if (!removed) {
			throw new GuardianLifeAssuranceException.ResourceNotFoundException(
					"Sorry, we couldn't find a query with ID: " + queryId + " for customer ID: " + customerId);
		}
		customerRepository.save(customer);
		return "Query with ID " + queryId + " has been successfully deleted for customer ID " + customerId + ".";
	}

	@Override
	public String respondToQuery(long queryId, QueryReplyDto queryReplyDto) {
		Query query = queryRepository.findById(queryId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Sorry, we couldn't find a query with ID: " + queryId));
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
				.map(query -> convertQueryToQueryResponseDto(query)).collect(Collectors.toList());
		return new PagedResponse<>(queryDtos, queryPage.getNumber(), queryPage.getSize(), queryPage.getTotalElements(),
				queryPage.getTotalPages(), queryPage.isLast());
	}

	@Override
	public Long buyPolicy(PolicyAccountRequestDto accountRequestDto, long customerId) {

		InsuranceScheme insuranceScheme = insuranceSchemeRepository.findById(accountRequestDto.getInsuranceSchemeId())
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Sorry, we couldn't find a scheme with ID: " + accountRequestDto.getInsuranceSchemeId()));
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		PolicyAccount policyAccount = new PolicyAccount();
		if (accountRequestDto.getAgentId() != 0) {
			Agent agent = agentRepository.findById(accountRequestDto.getAgentId())
					.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
							"Sorry, we couldn't find a agent with ID: " + accountRequestDto.getAgentId()));
			agent.setTotalCommission(agent.getTotalCommission()+insuranceScheme.getRegistrationCommRatio());
			agentRepository.save(agent);
			policyAccount.setAgent(agent);
			
		}

		List<DocumentType> requiredDocuments = insuranceScheme.getRequiredDocuments();
	    List<Document> customerDocuments = customer.getDocuments();

	    for (DocumentType requiredDoc : requiredDocuments) {
	        boolean hasRequiredDoc = customerDocuments.stream()
	            .anyMatch(doc -> doc.getDocumentName().equals(requiredDoc.name()) && doc.isVerified());

	        if (!hasRequiredDoc) {
	            throw new GuardianLifeAssuranceException.DocumentNotVerifiedException(
	                "The customer has not submitted or verified the required document: " + requiredDoc);
	        }
	    }

		policyAccount.setCustomer(customer);
		policyAccount.setPremiumType(accountRequestDto.getPremiumType());
		policyAccount.setPolicyTerm(accountRequestDto.getPolicyTerm());
		policyAccount.setPremiumAmount(accountRequestDto.getPremiumAmount());
		policyAccount.setMaturityDate(policyAccount.getIssueDate().plusYears(accountRequestDto.getPolicyTerm()));
		policyAccount.setSumAssured((policyAccount.getPremiumAmount() * (insuranceScheme.getProfitRatio() / 100))
				+ policyAccount.getPremiumAmount());
		long months;
		if (policyAccount.getPremiumType().equals(PremiumType.MONTHLY)) {
			months = 1;
		} else if (policyAccount.getPremiumType().equals(PremiumType.QUARTERLY)) {
			months = 3;
		} else if (policyAccount.getPremiumType().equals(PremiumType.HALF_YEARLY)) {
			months = 6;
		} else {
			months = 12;
		}
		double amount = policyAccount.getPremiumAmount();
		long totalMonths = (policyAccount.getPolicyTerm() * 12) / months;
		policyAccount.setInstallmentAmount(amount / totalMonths);
		policyAccount.setTotalPaidAmount(0.0);
		PolicyAccount savedPolicy = policyRepository.save(policyAccount);
		return savedPolicy.getPolicyNo();
	}

	@Override
	public PagedResponse<PolicyAccountResponseDto> getAllPoliciesByCustomerId(long customerId, int page, int size,
			String sortBy, String direction) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<PolicyAccount> policyAccountPage = policyRepository.findAllByCustomer(pageRequest, customer);
		List<PolicyAccountResponseDto> policies = policyAccountPage.getContent().stream()
				.map(policy -> convertPolicyToPolicyResponseDto(policy)).collect(Collectors.toList());
		return new PagedResponse<>(policies, policyAccountPage.getNumber(), policyAccountPage.getSize(),
				policyAccountPage.getTotalElements(), policyAccountPage.getTotalPages(), policyAccountPage.isLast());
	}

	private PolicyAccountResponseDto convertPolicyToPolicyResponseDto(PolicyAccount policy) {
		PolicyAccountResponseDto policyAccountResponseDto = new PolicyAccountResponseDto();
		if (policy.getAgent() != null)
			policyAccountResponseDto
					.setAgentName(policy.getAgent().getFirstName() + " " + policy.getAgent().getLastName());

		Customer customer = policy.getCustomer();
		policyAccountResponseDto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
		policyAccountResponseDto.setCustomerCity(customer.getAddress().getCity().getName());
		policyAccountResponseDto.setCustomerState(customer.getAddress().getCity().getState().getName());
		policyAccountResponseDto.setEmail(customer.getUser().getEmail());
		policyAccountResponseDto.setPhoneNumber(customer.getPhoneNumber());
		policyAccountResponseDto.setPolicyNo(policy.getPolicyNo());
		if (policy.getInsuranceScheme().getInsurancePlan() != null) {
			policyAccountResponseDto.setInsurancePlan(policy.getInsuranceScheme().getInsurancePlan().getPlanName());
		}
		policyAccountResponseDto.setInsuranceScheme(policy.getInsuranceScheme().getSchemeName());
		policyAccountResponseDto.setDateCreated(policy.getIssueDate());
		policyAccountResponseDto.setMaturityDate(policy.getMaturityDate());
		policyAccountResponseDto.setPremiumType(policy.getPremiumType());
		policyAccountResponseDto.setTotalPremiumAmount(policy.getPremiumAmount());
		policyAccountResponseDto.setProfitRatio(policy.getInsuranceScheme().getProfitRatio());
		policyAccountResponseDto.setSumAssured(policy.getSumAssured());
		policyAccountResponseDto.setInstallments(policy.getInstallments());
		return policyAccountResponseDto;
	}

	@Override
	public PolicyAccountResponseDto getPolicyById(long customerId, long policyId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		 PolicyAccount policy = customer.getPolicies().stream()
		            .filter(p -> p.getPolicyNo() == policyId)
		            .findFirst()
		            .orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
		                    "Sorry, we couldn't find a policy with ID: " + policyId));
		return convertPolicyToPolicyResponseDto(policy);
	}

}
