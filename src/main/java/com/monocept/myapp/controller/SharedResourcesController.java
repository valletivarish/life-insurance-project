package com.monocept.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.AgentRequestDto;
import com.monocept.myapp.dto.AgentResponseDto;
import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.dto.InsuranceSettingResponseDto;
import com.monocept.myapp.dto.TaxSettingResponseDto;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.EmployeeManagementService;
import com.monocept.myapp.service.SettingService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/GuardianLifeAssurance/shared")
public class SharedResourcesController {
	@Autowired
	private EmployeeManagementService employeeManagementService;

	@Autowired
	private AgentManagementService agentManagementService;
	
	@Autowired
	private SettingService settingService;

	@PutMapping("/employees")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@Operation(summary = "Update employee details", description = "Update the details of an existing employee")
	public ResponseEntity<EmployeeResponseDto> updateEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
		return new ResponseEntity<EmployeeResponseDto>(employeeManagementService.updateEmployee(employeeRequestDto),
				HttpStatus.OK);
	}

	@GetMapping("/employees/{employeesId}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<EmployeeResponseDto> getemployeesIdById(@PathVariable long employeesId) {
		return new ResponseEntity<EmployeeResponseDto>(employeeManagementService.getemployeesIdById(employeesId),
				HttpStatus.OK);
	}

	@GetMapping("/agents")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<PagedResponse<AgentResponseDto>> getAllAgents(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "agentId") String sortBy,
			@RequestParam(name = "direction") String direction) {
		return new ResponseEntity<PagedResponse<AgentResponseDto>>(
				agentManagementService.getAllAgents(page, size, sortBy, direction), HttpStatus.OK);
	}
	
	@PostMapping("/agents")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<String> createAgent(@RequestBody AgentRequestDto agentRequestDto) {
		return new ResponseEntity<String>(agentManagementService.createAgent(agentRequestDto), HttpStatus.CREATED);
	}

	@GetMapping("/agents/{agentId}")
	public ResponseEntity<AgentResponseDto> getAgentById(@PathVariable long agentId) {
		return new ResponseEntity<AgentResponseDto>(agentManagementService.getAgentById(agentId), HttpStatus.OK);
	}

	@PutMapping("/agents")
	public ResponseEntity<String> updateAgent(@RequestBody AgentRequestDto agentRequestDto) {
		return new ResponseEntity<String>(agentManagementService.updateAgent(agentRequestDto), HttpStatus.OK);
	}
	
	@GetMapping("/insurance-setting")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<InsuranceSettingResponseDto> getInsuranceSetting(){
		return new ResponseEntity<InsuranceSettingResponseDto>(settingService.getInsuranceSetting(),HttpStatus.OK);
	}
	@GetMapping("/tax-setting")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<TaxSettingResponseDto> getTaxSetting(){
		return new ResponseEntity<TaxSettingResponseDto>(settingService.getTaxSetting(),HttpStatus.OK);
	}
}
