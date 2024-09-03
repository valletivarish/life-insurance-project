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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.entity.Employee;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.StudentApiException;
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
	private EmployeeRepository employeeRepository;

	public String createEmployee(EmployeeRequestDto employeeRequestDto) {
		if (userRepository.existsByUsername(employeeRequestDto.getUsername())) {
			throw new StudentApiException(HttpStatus.BAD_REQUEST, "Username already exists!");
		}

		if (userRepository.existsByEmail(employeeRequestDto.getEmail())) {
			throw new StudentApiException(HttpStatus.BAD_REQUEST, "Email already exists!");
		}
		User user = new User();
		user.setUsername(employeeRequestDto.getUsername());
		user.setEmail(employeeRequestDto.getEmail());
		user.setPassword(passwordEncoder.encode(employeeRequestDto.getPassword()));

		Set<Role> roles = new HashSet<>();
		String roleName="ROLE_EMPLOYEE";
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new StudentApiException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));
		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);
		Employee employee = new Employee();
		employee.setUser(user);
		employee.setFirstName(employeeRequestDto.getFirstName());
		employee.setLastName(employeeRequestDto.getLastName());
		employee.setSalary(employeeRequestDto.getSalary());
		employeeRepository.save(employee);

		return "Employee Registered successfully";
	}

	@Override
	public PagedResponse<EmployeeResponseDto> getAllEmployees(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<Employee> employeesPage = employeeRepository.findAll(pageRequest);
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
		employeeResponseDto.setName(employee.getFirstName());
		employeeResponseDto.setStatus(employee.isActive());

		return employeeResponseDto;
	}

	@Override
	public EmployeeResponseDto updateEmployee(EmployeeRequestDto employeeRequestDto) {
		Employee employee = employeeRepository.findById(employeeRequestDto.getEmployeeId())
				.orElseThrow(() -> new StudentApiException(HttpStatus.NOT_FOUND, "employee not found"));
		User user = employee.getUser();
		if (employeeRequestDto.getEmail() != null && employeeRequestDto.getEmail() != "") {
			user.setEmail(employeeRequestDto.getEmail());
		}
		employee.setActive(employeeRequestDto.isActive());
		if (employeeRequestDto.getFirstName() != null && employeeRequestDto.getFirstName() != "") {
			employee.setFirstName(employeeRequestDto.getFirstName());
		}
		userRepository.save(user);
		return convertEmployeeToEmployeeResponseDto(employeeRepository.save(employee));
	}

	@Override
	public String deactivateEmployee(long employeeId) {
		Employee employee = employeeRepository.findById(employeeId).orElseThrow(()->new StudentApiException(HttpStatus.OK, "Employee Not found"));
		if(!employee.isActive()) {
			throw new StudentApiException(HttpStatus.CONFLICT, "Employee is already deleted");
		}
		employee.setActive(false);
		employeeRepository.save(employee);
		return "Employee Deleted Successfully";
	}

}
