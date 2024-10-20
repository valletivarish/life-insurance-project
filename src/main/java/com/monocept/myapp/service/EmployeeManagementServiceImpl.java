package com.monocept.myapp.service;

import java.util.HashSet;
import java.util.List;
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

import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.entity.Document;
import com.monocept.myapp.entity.Employee;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException.UserAlreadyDeactivatedException;
import com.monocept.myapp.repository.DocumentRepository;
import com.monocept.myapp.repository.EmployeeRepository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.util.PagedResponse;

@Service
public class EmployeeManagementServiceImpl implements EmployeeManagementService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	public String createEmployee(EmployeeRequestDto employeeRequestDto) {
		if (userRepository.existsByUsername(employeeRequestDto.getUsername())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Username already exists!");
		}

		if (userRepository.existsByEmail(employeeRequestDto.getEmail())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Email already exists!");
		}
		User user = new User();
		user.setUsername(employeeRequestDto.getUsername());
		user.setEmail(employeeRequestDto.getEmail());
		user.setPassword(passwordEncoder.encode(employeeRequestDto.getPassword()));

		Set<Role> roles = new HashSet<>();
		String roleName = "ROLE_EMPLOYEE";
		Role role = roleRepository.findByName(roleName).orElseThrow(
				() -> new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));
		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);
		Employee employee = new Employee();
		employee.setUser(user);
		employee.setFirstName(employeeRequestDto.getFirstName());
		employee.setLastName(employeeRequestDto.getLastName());
		employee.setSalary(employeeRequestDto.getSalary());
		employeeRepository.save(employee);

		return "Employee " + employeeRequestDto.getFirstName() + " " + employeeRequestDto.getLastName() + " has been successfully registered.";
	}

	@Override
	public PagedResponse<EmployeeResponseDto> getAllEmployees(int page, int size, String sortBy, String direction,String name,Boolean isActive) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<Employee> employeesPage = employeeRepository.findAllByCriteria(name,isActive,pageRequest);
		List<EmployeeResponseDto> employees = employeesPage.getContent().stream()
				.map(employee -> convertEmployeeToEmployeeResponseDto(employee)).collect(Collectors.toList());

		return new PagedResponse<>(employees, employeesPage.getNumber(), employeesPage.getSize(),
				employeesPage.getTotalElements(), employeesPage.getTotalPages(), employeesPage.isLast());
	}

	private EmployeeResponseDto convertEmployeeToEmployeeResponseDto(Employee employee) {
		EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();
		employeeResponseDto.setUsername(employee.getUser().getUsername());
		employeeResponseDto.setUserId(employee.getUser().getUserId());
		employeeResponseDto.setEmployeeId(employee.getEmployeeId());
		employeeResponseDto.setFirstName(employee.getFirstName());
		employeeResponseDto.setLastName(employee.getLastName());
		employeeResponseDto.setEmail(employee.getUser().getEmail());
		employeeResponseDto.setStatus(employee.isActive());

		return employeeResponseDto;
	}

	@Override
	public String updateEmployee(EmployeeRequestDto employeeRequestDto) {
		Employee employee = employeeRepository.findById(employeeRequestDto.getEmployeeId())
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException("Sorry, we couldn't find an employee with ID: " + employeeRequestDto.getEmployeeId()));
		User user = employee.getUser();
		if (employeeRequestDto.getEmail() != null && employeeRequestDto.getEmail() != "") {
			user.setEmail(employeeRequestDto.getEmail());
		}
		employee.setActive(employeeRequestDto.isActive());
		if (employeeRequestDto.getFirstName() != null && employeeRequestDto.getFirstName() != "") {
			employee.setFirstName(employeeRequestDto.getFirstName());
		}
		userRepository.save(user);
		convertEmployeeToEmployeeResponseDto(employeeRepository.save(employee));
		return "Employee with id "+employeeRequestDto.getEmployeeId()+" updated successfully";
	}

	@Override
	public String deactivateEmployee(long employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException("Sorry, we couldn't find an employee with ID: " + employeeId));
		if (!employee.isActive()) {
			throw new UserAlreadyDeactivatedException("Employee with ID " + employeeId + " is already deactivated.");
		}
		employee.setActive(false);
		employeeRepository.save(employee);
		return "Employee Deleted Successfully";
	}

	@Override
	public EmployeeResponseDto getemployeesIdById(long employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException("Sorry, we couldn't find an employee with ID: " + employeeId));

		return convertEmployeeToEmployeeResponseDto(employee);
	}

	public String verifyDocument(int documentId) {
	    Document document = documentRepository.findById(documentId)
	            .orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException("Sorry, we couldn't find a document with ID: " + documentId));

	    String username = getEmailFromSecurityContext();
	    System.out.println("Username from Security Context: " + username);
	    
	    User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
	    if (user == null) {
	        throw new GuardianLifeAssuranceException.ResourceNotFoundException("User not found for the given username.");
	    }

	    Employee employee = employeeRepository.findByUser(user);
	    if (employee == null) {
	        throw new GuardianLifeAssuranceException.ResourceNotFoundException("Employee not found for the given user.");
	    }

	    document.setVerified(true);
	    document.setVerifyBy(employee);
	    documentRepository.save(document);

	    return "Document with ID " + documentId + " has been successfully verified by employee " + employee.getFirstName() + " " + employee.getLastName() + ".";
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
	public String rejectDocument(int documentId) {
	    Document document = documentRepository.findById(documentId)
	            .orElseThrow(() -> new GuardianLifeAssuranceException.ResourceNotFoundException("Sorry, we couldn't find a document with ID: " + documentId));

	    String username = getEmailFromSecurityContext();
	    System.out.println("Username from Security Context: " + username);

	    User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
	    if (user == null) {
	        throw new GuardianLifeAssuranceException.ResourceNotFoundException("User not found for the given username.");
	    }

	    Employee employee = employeeRepository.findByUser(user);
	    if (employee == null) {
	        throw new GuardianLifeAssuranceException.ResourceNotFoundException("Employee not found for the given user.");
	    }

	    document.setVerified(false);
	    document.setVerifyBy(employee);
	    documentRepository.save(document);

	    return "The document with ID " + documentId + " has been reviewed and rejected by employee " + employee.getFirstName() + " " + employee.getLastName() + ". Please provide a clearer document for further processing.";
	}

	@Override
	public EmployeeResponseDto getEmployeeProfile() {
		User user = userRepository.findByUsernameOrEmail(getUserNameOrEmailFromSecurityContext(), getUserNameOrEmailFromSecurityContext()).orElseThrow(()->new GuardianLifeAssuranceException.UserNotFoundException("User not found"));
		return convertEmployeeToEmployeeResponseDto(employeeRepository.findByUser(user));
	}
	private String getUserNameOrEmailFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}



}
