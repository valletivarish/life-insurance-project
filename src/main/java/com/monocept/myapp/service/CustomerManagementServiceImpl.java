package com.monocept.myapp.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.CustomerSideQueryRequestDto;
import com.monocept.myapp.dto.InstallmentResponseDto;
import com.monocept.myapp.dto.PaymentResponseDto;
import com.monocept.myapp.dto.PolicyAccountRequestDto;
import com.monocept.myapp.dto.PolicyAccountResponseDto;
import com.monocept.myapp.dto.QueryReplyDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.entity.Address;
import com.monocept.myapp.entity.Agent;
import com.monocept.myapp.entity.City;
import com.monocept.myapp.entity.Commission;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.CustomerQuery;
import com.monocept.myapp.entity.Document;
import com.monocept.myapp.entity.Employee;
import com.monocept.myapp.entity.Installment;
import com.monocept.myapp.entity.InsuranceScheme;
import com.monocept.myapp.entity.InsuranceSetting;
import com.monocept.myapp.entity.Payment;
import com.monocept.myapp.entity.PolicyAccount;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.State;
import com.monocept.myapp.entity.TaxSetting;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.entity.WithdrawalRequest;
import com.monocept.myapp.enums.CommissionType;
import com.monocept.myapp.enums.DocumentType;
import com.monocept.myapp.enums.InstallmentStatus;
import com.monocept.myapp.enums.PaymentStatus;
import com.monocept.myapp.enums.PolicyStatus;
import com.monocept.myapp.enums.PremiumType;
import com.monocept.myapp.enums.WithdrawalRequestStatus;
import com.monocept.myapp.enums.WithdrawalRequestType;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException.UserAlreadyDeactivatedException;
import com.monocept.myapp.repository.AddressRepository;
import com.monocept.myapp.repository.AgentRepository;
import com.monocept.myapp.repository.CommissionRepository;
import com.monocept.myapp.repository.CustomerRepository;
import com.monocept.myapp.repository.DocumentRepository;
import com.monocept.myapp.repository.EmployeeRepository;
import com.monocept.myapp.repository.InstallmentRepository;
import com.monocept.myapp.repository.InsuranceSchemeRepository;
import com.monocept.myapp.repository.InsuranceSettingRepository;
import com.monocept.myapp.repository.PaymentRepository;
import com.monocept.myapp.repository.PolicyRepository;
import com.monocept.myapp.repository.QueryRepository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.StateRepository;
import com.monocept.myapp.repository.TaxSettingRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.repository.WithdrawalRepository;
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

	@Autowired
	private InstallmentRepository installmentRepository;
	@Autowired
	private TaxSettingRepository taxSettingRepository;
	@Autowired
	private InsuranceSettingRepository insuranceSettingRepository;

	@Autowired
	private CommissionRepository commissionRepository;
	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private WithdrawalRepository withdrawalRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

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

		Address address = new Address();

		State state = stateRepository.findById(customerRequestDto.getStateId()).orElse(null);
		List<City> cities = state.getCity();
		City city = cities.stream().filter(c -> c.getCityId() == customerRequestDto.getCityId()).findFirst()
				.orElse(null);
		address.setCity(city);
		address.setHouseNo(customerRequestDto.getHouseNo());
		address.setPincode(customerRequestDto.getPincode());
		address.setApartment(customerRequestDto.getApartment());

		customer.setActive(customerRequestDto.isActive());
		customer.setAddress(address);
		customer.setActive(true);
		customer.setDateOfBirth(customerRequestDto.getDateOfBirth());
		customer.setFirstName(customerRequestDto.getFirstName());
		customer.setLastName(customerRequestDto.getLastName());
		customer.setPhoneNumber(customerRequestDto.getPhoneNumber());
		customer.setUser(user);
		addressRepository.save(address);
		userRepository.save(user);
		return customerRepository.save(customer);
	}

	@Override
	public String updateCustomer(CustomerRequestDto customerRequestDto) {
		convertCustomerRequestDtoToCustomerAndUpdate(customerRequestDto);
		return "The Customer with ID " + customerRequestDto.getCustomerId() + " has been successfully updated.";
	}

	private void convertCustomerRequestDtoToCustomerAndUpdate(CustomerRequestDto customerRequestDto) {
		Customer customer = customerRepository.findById(customerRequestDto.getCustomerId())
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerRequestDto.getCustomerId()));
		Address address = customer.getAddress();
		State state = stateRepository.findById(customerRequestDto.getStateId()).orElse(null);
		List<City> cities = state.getCity();
		City city = cities.stream().filter(c -> c.getCityId() == customerRequestDto.getCityId()).findFirst()
				.orElse(null);
		address.setCity(city);
		address.setHouseNo(customerRequestDto.getHouseNo());
		address.setPincode(customerRequestDto.getPincode());
		address.setApartment(customerRequestDto.getApartment());
		customer.setDateOfBirth(customerRequestDto.getDateOfBirth());
		customer.setFirstName(customerRequestDto.getFirstName());
		customer.setLastName(customerRequestDto.getLastName());
		customer.setPhoneNumber(customerRequestDto.getPhoneNumber());
		addressRepository.save(address);
		customerRepository.save(customer);
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
			throw new UserAlreadyDeactivatedException(
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
		customerResponseDto.setDateOfBirth(customer.getDateOfBirth());
		customerResponseDto.setPhoneNumber(customer.getPhoneNumber());
		customerResponseDto.setHouseNo(customer.getAddress().getHouseNo());
		customerResponseDto.setPincode(customer.getAddress().getPincode());
		customerResponseDto.setApartment(customer.getAddress().getApartment());
		customerResponseDto.setState(customer.getAddress().getCity().getState().getName());
		customerResponseDto.setCity(customer.getAddress().getCity().getName());
		customerResponseDto.setPolicyAccounts(customer.getPolicies().stream()
				.map(policy -> convertPolicyToPolicyResponseDto(policy)).collect(Collectors.toList()));

		return customerResponseDto;
	}

	@Override
	public String uploadDocument(MultipartFile file, DocumentType documentName, long customerId) throws IOException {
	    Customer customer = customerRepository.findById(customerId)
	            .orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
	                    "Sorry, we couldn't find a customer with ID: " + customerId));

	    Optional<Document> existingDocument = documentRepository.findByCustomerAndDocumentName(customer, documentName);

	    if (existingDocument.isPresent()) {
	        Document document = existingDocument.get();
	        if (!document.isVerified()) {
	            document.setContent(ImageUtil.compressFile(file.getBytes()));
	            document.setVerifyBy(null); 
	            document.setVerified(false); 
	            documentRepository.save(document);
	            return "Document '" + documentName + "' has been successfully updated for customer ID " + customerId + ".";
	        } else {
	            throw new GuardianLifeAssuranceException("Document '" + documentName + "' is already verified. Please contact support for further assistance.");
	        }
	    } else {
	        // No existing document of this type, proceed with a new upload
	        Document newDocument = new Document();
	        newDocument.setCustomer(customer);
	        newDocument.setContent(ImageUtil.compressFile(file.getBytes()));
	        newDocument.setDocumentName(documentName);
	        documentRepository.save(newDocument);
	        return "Document '" + documentName + "' has been successfully uploaded for customer ID " + customerId + ".";
	    }
	}


	@Override
	public String createCustomerQuery(long customerId, CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));

		CustomerQuery query = new CustomerQuery();
		query.setCustomer(customer);
		query.setMessage(customerSideQueryRequestDto.getMessage());
		query.setTitle(customerSideQueryRequestDto.getTitle());

		customer.getQueries().add(query);

		customerRepository.save(customer);

		return "Your query titled '" + customerSideQueryRequestDto.getTitle() + "' has been successfully submitted.";
	}

	@Override
	public String updateCustomerQuery(long customerId, CustomerSideQueryRequestDto customerSideQueryRequestDto) {
		customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		CustomerQuery existingQuery = queryRepository.findById(customerSideQueryRequestDto.getId())
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
		Page<CustomerQuery> queryPage = queryRepository.findByCustomer(customer, pageRequest);
		List<QueryResponseDto> queries = queryPage.getContent().stream()
				.map(query -> convertQueryToQueryResponseDto(query)).collect(Collectors.toList());
		return new PagedResponse<QueryResponseDto>(queries, queryPage.getNumber(), queryPage.getSize(),
				queryPage.getTotalElements(), queryPage.getTotalPages(), queryPage.isLast());
	}

	private QueryResponseDto convertQueryToQueryResponseDto(CustomerQuery query) {
		QueryResponseDto queryResponseDto = new QueryResponseDto();
		queryResponseDto.setQueryId(query.getQueryId());
		queryResponseDto.setCustomerId(query.getCustomer().getCustomerId());
		queryResponseDto.setCustomerName(query.getCustomer().getFirstName() + " " + query.getCustomer().getLastName());
		queryResponseDto.setMessage(query.getMessage());
		queryResponseDto.setResolved(query.isResolved());
		queryResponseDto.setTitle(query.getTitle());
		if (query.getResolvedBy() != null) {
			queryResponseDto
					.setResolvedBy(query.getResolvedBy().getFirstName() + " " + query.getResolvedBy().getLastName());
		}
		queryResponseDto.setResolvedAt(query.getResolvedAt().toLocalDate());
		queryResponseDto.setResponse(query.getResponse() != null ? query.getResponse() : "");
		return queryResponseDto;
	}

	@Override
	public String deleteQuery(long customerId, long queryId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));

		CustomerQuery queryToDelete = customer.getQueries().stream().filter(query -> query.getQueryId() == queryId)
				.findFirst().orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Customer doesn't have the following query with id " + queryId));

		customer.getQueries().remove(queryToDelete);
		customerRepository.save(customer);

		return "Query with ID " + queryId + " has been successfully deleted for customer ID " + customerId + ".";
	}

	@Override
	public String respondToQuery(long queryId, QueryReplyDto queryReplyDto) {
		CustomerQuery query = queryRepository.findById(queryId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Sorry, we couldn't find a query with ID: " + queryId));
		query.setResolved(true);
		User user = userRepository.findByEmail(getEmailFromSecurityContext()).orElse(null);
		Employee employee = employeeRepository.findByUser(user);
		query.setResolvedBy(employee);
		query.setResponse(queryReplyDto.getResponse());
		queryRepository.save(query);
		return "Response to query ID " + queryId + " has been successfully recorded.";
	}

	private String getEmailFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}

	@Override
	public PagedResponse<QueryResponseDto> getAllQueries(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<CustomerQuery> queryPage = queryRepository.findAll(pageRequest);
		List<QueryResponseDto> queryDtos = queryPage.getContent().stream()
				.map(query -> convertQueryToQueryResponseDto(query)).collect(Collectors.toList());
		return new PagedResponse<>(queryDtos, queryPage.getNumber(), queryPage.getSize(), queryPage.getTotalElements(),
				queryPage.getTotalPages(), queryPage.isLast());
	}

	@Override
	public Long processPolicyPurchase(PolicyAccountRequestDto accountRequestDto, long customerId) {
		TaxSetting taxSetting = taxSettingRepository.findTopByOrderByUpdatedAtDesc();
		InsuranceSetting insuranceSetting = insuranceSettingRepository.findTopByOrderByUpdatedAtDesc();

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));

		InsuranceScheme insuranceScheme = insuranceSchemeRepository.findById(accountRequestDto.getInsuranceSchemeId())
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Sorry, we couldn't find a scheme with ID: " + accountRequestDto.getInsuranceSchemeId()));

		verifyDocuments(insuranceScheme.getRequiredDocuments(), customer.getCustomerId());

		long months = calculateMonths(accountRequestDto.getPremiumType());
		double installmentAmount = calculateInstallmentAmount(accountRequestDto.getPremiumAmount(),
				accountRequestDto.getPolicyTerm(), taxSetting, months);

		PolicyAccount policyAccount = createPolicyAccount(accountRequestDto, customer, insuranceScheme, taxSetting,
				insuranceSetting, installmentAmount);

		if (accountRequestDto.getAgentId() != null && accountRequestDto.getAgentId()!=0) {
			handleAgentCommission(accountRequestDto.getAgentId(), insuranceScheme, policyAccount);
		}

		handlePaymentsAndInstallments(policyAccount, installmentAmount, months, accountRequestDto.getPolicyTerm(),
				accountRequestDto.getStripeToken());

		savePolicyToCustomer(policyAccount, customer);

		return policyAccount.getPolicyNo();
	}

	private void handleAgentCommission(long agentId, InsuranceScheme insuranceScheme, PolicyAccount policyAccount) {
		Agent agent = agentRepository.findById(agentId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find an agent with ID: " + agentId));

		agent.setTotalCommission(agent.getTotalCommission() + insuranceScheme.getRegistrationCommRatio());

		Commission commission = new Commission();
		commission.setAgent(agent);
		commission.setAmount(insuranceScheme.getRegistrationCommRatio());
		commission.setCommissionType(CommissionType.REGISTRATION);
		commissionRepository.save(commission);

		List<Commission> commissions = agent.getCommissions();
		if (commissions == null) {
			commissions = new ArrayList<>();
		}
		commissions.add(commission);
		agent.setCommissions(commissions);

		agentRepository.save(agent);

		policyAccount.setAgent(agent);
	}

	private void savePolicyToCustomer(PolicyAccount policyAccount, Customer customer) {
		List<PolicyAccount> policies = customer.getPolicies();
		if (policies == null) {
			policies = new ArrayList<>();
		}
		policies.add(policyAccount);
		customer.setPolicies(policies);
		customerRepository.save(customer);
	}

	private void verifyDocuments(List<DocumentType> requiredDocuments, Long customerId) {
		List<Document> verifiedDocuments = documentRepository.findVerifiedDocumentsByCustomerId(customerId);

		for (DocumentType requiredDoc : requiredDocuments) {
			boolean hasRequiredDoc = verifiedDocuments.stream()
					.anyMatch(doc -> requiredDoc.name().equalsIgnoreCase(doc.getDocumentName().name()));

			if (!hasRequiredDoc) {
				throw new GuardianLifeAssuranceException.DocumentNotVerifiedException(
						"The customer has not submitted or verified the required document: " + requiredDoc.name());
			}
		}
	}

	private long calculateMonths(PremiumType premiumType) {
		switch (premiumType) {
		case MONTHLY:
			return 1;
		case QUARTERLY:
			return 3;
		case HALF_YEARLY:
			return 6;
		default:
			return 12;
		}
	}

	private double calculateInstallmentAmount(double premiumAmount, long policyTerm, TaxSetting taxSetting,
			long months) {
		long totalMonths = (policyTerm * 12) / months;
		return (premiumAmount / totalMonths) * (taxSetting.getTaxPercentage() / 100) + (premiumAmount / totalMonths);
	}


	private PolicyAccount createPolicyAccount(PolicyAccountRequestDto accountRequestDto, Customer customer,
			InsuranceScheme insuranceScheme, TaxSetting taxSetting, InsuranceSetting insuranceSetting,
			double installmentAmount) {
		PolicyAccount policyAccount = new PolicyAccount();
		policyAccount.setCustomer(customer);
		policyAccount.setPremiumType(accountRequestDto.getPremiumType());
		policyAccount.setPolicyTerm(accountRequestDto.getPolicyTerm());
		policyAccount.setPremiumAmount(accountRequestDto.getPremiumAmount());
		policyAccount.setMaturityDate(policyAccount.getIssueDate().plusYears(accountRequestDto.getPolicyTerm()));
		policyAccount.setSumAssured((accountRequestDto.getPremiumAmount() * (insuranceScheme.getProfitRatio() / 100))
				+ accountRequestDto.getPremiumAmount());
		policyAccount.setInstallmentAmount(installmentAmount);
		policyAccount.setTotalPaidAmount(installmentAmount);
		policyAccount.setInsuranceScheme(insuranceScheme);
		policyAccount.setInsuranceSetting(insuranceSetting);
		policyAccount.setTaxSetting(taxSetting);

		return policyRepository.save(policyAccount);
	}

	private void handlePaymentsAndInstallments(PolicyAccount policyAccount, double installmentAmount, long months,
			long policyTerm, String chargeId) {
		Payment payment = new Payment();
		payment.setAmount(installmentAmount);
		payment.setChargeId(chargeId);
		payment.setCustomerId(policyAccount.getCustomer().getCustomerId());
		payment.setStatus(PaymentStatus.PAID);
		payment.setPolicyAccount(policyAccount);

		List<Payment> payments = policyAccount.getPayments();
		if (payments == null) {
			payments = new ArrayList<>();
		}
		payments.add(payment);
		policyAccount.setPayments(payments);
		paymentRepository.save(payment);

		LocalDate startDate = LocalDate.now();
		Installment firstInstallment = new Installment();
		firstInstallment.setInsurancePolicy(policyAccount);
		firstInstallment.setDueDate(startDate);
		firstInstallment.setAmountDue(installmentAmount);
		firstInstallment.setAmountPaid(installmentAmount);
		firstInstallment.setPaymentDate(startDate);
		firstInstallment.setPaymentReference(chargeId);
		firstInstallment.setStatus(InstallmentStatus.PAID);
		installmentRepository.save(firstInstallment);

		long totalMonths = (policyTerm * 12) / months;
		for (int i = 1; i < totalMonths; i++) {
			Installment installment = new Installment();
			installment.setInsurancePolicy(policyAccount);
			installment.setDueDate(startDate.plusMonths(i * months));
			installment.setAmountDue(installmentAmount);
			installment.setStatus(InstallmentStatus.PENDING);
			installmentRepository.save(installment);
		}
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
		PolicyAccountResponseDto policyDto = new PolicyAccountResponseDto();

		if (policy.getAgent() != null) {
			policyDto.setAgentName(policy.getAgent().getFirstName() + " " + policy.getAgent().getLastName());
		}

		Customer customer = policy.getCustomer();
		policyDto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
		policyDto.setCustomerCity(customer.getAddress().getCity().getName());
		policyDto.setCustomerState(customer.getAddress().getCity().getState().getName());
		policyDto.setEmail(customer.getUser().getEmail());
		policyDto.setPhoneNumber(customer.getPhoneNumber());
		policyDto.setPolicyNo(policy.getPolicyNo());
		policyDto.setPolicyStatus(policy.getStatus());

		if (policy.getInsuranceScheme().getInsurancePlan() != null) {
			policyDto.setInsurancePlan(policy.getInsuranceScheme().getInsurancePlan().getPlanName());
		}

		policyDto.setInsuranceScheme(policy.getInsuranceScheme().getSchemeName());
		policyDto.setDateCreated(policy.getIssueDate());
		policyDto.setMaturityDate(policy.getMaturityDate());
		policyDto.setPremiumType(policy.getPremiumType());
		policyDto.setPremiumAmount(policy.getPremiumAmount());
		policyDto.setProfitRatio(policy.getInsuranceScheme().getProfitRatio());
		policyDto.setSumAssured(policy.getSumAssured());

		List<InstallmentResponseDto> installmentDtos = policy.getInstallments().stream()
				.map(this::convertInstallmentToDto).collect(Collectors.toList());
		policyDto.setInstallments(installmentDtos);

		List<PaymentResponseDto> paymentDtos = policy.getPayments().stream().map(this::convertPaymentToDto)
				.collect(Collectors.toList());
		policyDto.setPayments(paymentDtos);

		return policyDto;
	}

	private InstallmentResponseDto convertInstallmentToDto(Installment installment) {
		InstallmentResponseDto installmentDto = new InstallmentResponseDto();
		installmentDto.setInstallmentId(installment.getInstallmentId());
		installmentDto.setDueDate(installment.getDueDate());
		installmentDto.setAmountDue(installment.getAmountDue());
		installmentDto.setPaymentDate(installment.getPaymentDate());
		installmentDto.setAmountPaid(installment.getAmountPaid());
		installmentDto.setStatus(installment.getStatus());
		return installmentDto;
	}

	private PaymentResponseDto convertPaymentToDto(Payment payment) {
		PaymentResponseDto paymentDto = new PaymentResponseDto();
		paymentDto.setPaymentId(payment.getPaymentId());
		paymentDto.setAmount(payment.getAmount());
		paymentDto.setStatus(payment.getStatus());
		paymentDto.setPaymentDate(payment.getPaymentDate().toLocalDate());
		return paymentDto;
	}

	@Override
	public PolicyAccountResponseDto getPolicyById(long customerId, long policyNo) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		PolicyAccount policy = customer.getPolicies().stream().filter(p -> p.getPolicyNo() == policyNo).findFirst()
				.orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Sorry, we couldn't find a policy with ID: " + policyNo));
		return convertPolicyToPolicyResponseDto(policy);
	}

	@Override
	public String cancelPolicy(long customerId, long policyNo) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));

		PolicyAccount policyAccount = customer.getPolicies().stream().filter(p -> p.getPolicyNo() == policyNo)
				.findFirst().orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException(
						"Sorry, we couldn't find a policy with number: " + policyNo));

		if (policyAccount.getStatus().equals(PolicyStatus.DROPPED)) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST,
					"This policy is already canceled or dropped.");
		}

		InsuranceSetting insuranceSetting = policyAccount.getInsuranceSetting();
		double penaltyAmount = insuranceSetting.getPenaltyAmount();

		policyAccount.setStatus(PolicyStatus.DROPPED);
		policyAccount.setCancellationDate(LocalDateTime.now());

		List<Installment> unpaidInstallments = installmentRepository.findByInsurancePolicyAndStatus(policyAccount,
				InstallmentStatus.PENDING);
		unpaidInstallments.forEach(installment -> {
			installment.setStatus(InstallmentStatus.CANCELED);
			installmentRepository.save(installment);
		});

		double refundAmount = calculateProratedRefund(policyAccount) - penaltyAmount;
		if (refundAmount < 0) {
			refundAmount = 0;
		}

		createWithdrawalRequest(customer, policyAccount, refundAmount);

		policyRepository.save(policyAccount);
		return "Policy has been canceled successfully. A withdrawal request has been created.";
	}

	private void createWithdrawalRequest(Customer customer, PolicyAccount policyAccount, double refundAmount) {
		WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
		withdrawalRequest.setCustomer(customer);
		withdrawalRequest.setAmount(refundAmount);
		withdrawalRequest.setRequestType(WithdrawalRequestType.POLICY_CANCELLATION);
		withdrawalRequest.setRequestDate(LocalDateTime.now());
		withdrawalRequest.setStatus(WithdrawalRequestStatus.PENDING);

		withdrawalRepository.save(withdrawalRequest);
	}

	private double calculateProratedRefund(PolicyAccount policyAccount) {
		long totalMonths = policyAccount.getPolicyTerm() * 12;
		long usedMonths = ChronoUnit.MONTHS.between(policyAccount.getIssueDate(), LocalDate.now());
		double unusedAmount = (policyAccount.getInstallmentAmount() * (totalMonths - usedMonths)) / totalMonths;

		return unusedAmount;
	}

	@Override
	public PagedResponse<CustomerResponseDto> getAllCustomersWithFilters(int page, int size, String sortBy,
			String direction, String name, String city, String state, Boolean isActive) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);

		Page<Customer> customerPage = customerRepository.findByFilters(name, city, state, isActive, pageRequest);
		List<CustomerResponseDto> customers = customerPage.getContent().stream()
				.map(this::convertCustomerToCustomerResponseDto).collect(Collectors.toList());

		return new PagedResponse<>(customers, customerPage.getNumber(), customerPage.getSize(),
				customerPage.getTotalElements(), customerPage.getTotalPages(), customerPage.isLast());
	}

	@Override
	public String activateCustomer(long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException(
						"Sorry, we couldn't find a customer with ID: " + customerId));
		if (customer.isActive()) {
			throw new UserAlreadyDeactivatedException("The customer with ID " + customerId + " is already activated.");
		}
		customer.setActive(true);
		customerRepository.save(customer);
		return "The customer with ID " + customerId + " has been successfully activated.";
	}

	@Override
	public PagedResponse<PolicyAccountResponseDto> getAllPolicies(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<PolicyAccount> policyAccountPage = policyRepository.findAll(pageRequest);
		System.out.println(policyAccountPage);
		List<PolicyAccountResponseDto> policies = policyAccountPage.getContent().stream()
				.map(policy -> convertPolicyToPolicyResponseDto(policy)).collect(Collectors.toList());
		System.out.println(policies);
		return new PagedResponse<>(policies, policyAccountPage.getNumber(), policyAccountPage.getSize(),
				policyAccountPage.getTotalElements(), policyAccountPage.getTotalPages(), policyAccountPage.isLast());
	}

	public PagedResponse<QueryResponseDto> getAllQueries(int page, int size, String sortBy, String direction,
			String search, Boolean resolved) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));

		// Example search specification or query logic
		Page<CustomerQuery> queryPage = queryRepository.findAllByCriteria(search, resolved, pageable);

		List<QueryResponseDto> queries = queryPage.getContent().stream()
				.map(query -> convertQueryToQueryResponseDto(query)).collect(Collectors.toList());

		return new PagedResponse<>(queries, queryPage.getNumber(), queryPage.getSize(), queryPage.getTotalElements(),
				queryPage.getTotalPages(), queryPage.isLast());
	}

	@Override
	public int calculateCustomerAgeByUser(String username) {
		User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
		Customer customer = customerRepository.findByUser(user);
		LocalDate dateOfBirth = customer.getDateOfBirth();
		return calculateAge(dateOfBirth);
	}

	private int calculateAge(LocalDate dob) {
		return Period.between(dob, LocalDate.now()).getYears();
	}

	@Override
	public PagedResponse<CustomerResponseDto> getAllCustomersByAgentWithFilters(int page, int size, String sortBy,
			String direction, String name, String city, String state, Boolean isActive) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);

		Page<Customer> customerPage = customerRepository.findByAgentFilters(getAgentFromSecurityContext().getAgentId(), name, city, state, isActive, pageRequest);
		List<CustomerResponseDto> customers = customerPage.getContent().stream()
				.map(this::convertCustomerToCustomerResponseDto).collect(Collectors.toList());

		return new PagedResponse<>(customers, customerPage.getNumber(), customerPage.getSize(),
				customerPage.getTotalElements(), customerPage.getTotalPages(), customerPage.isLast());
	}
	
	private Agent getAgentFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			return agentRepository.findByUser(
				    userRepository.findByUsernameOrEmail(
				        userDetails.getUsername(), 
				        userDetails.getUsername()
				    ).orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException("User not found"))
				);
		}
		throw new GuardianLifeAssuranceException.UserNotFoundException("agent not found");
	}

}
