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
import com.monocept.myapp.dto.QueryReplyDto;
import com.monocept.myapp.dto.QueryResponseDto;
import com.monocept.myapp.dto.TaxSettingResponseDto;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.CustomerManagementService;
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
	
	@Autowired
	private CustomerManagementService customerManagementService;

	@PutMapping("/employees")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@Operation(summary = "Update employee details", description = "Update the details of an existing employee")
	public ResponseEntity<EmployeeResponseDto> updateEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
		return new ResponseEntity<EmployeeResponseDto>(employeeManagementService.updateEmployee(employeeRequestDto),
				HttpStatus.OK);
	}

	@GetMapping("/employees/{employeesId}")

	@Operation(summary = "get employee by id", description = "")

	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<EmployeeResponseDto> getemployeesIdById(@PathVariable long employeesId) {
		return new ResponseEntity<EmployeeResponseDto>(employeeManagementService.getemployeesIdById(employeesId),
				HttpStatus.OK);
	}

	@GetMapping("/agents")
	@Operation(summary = "get all agents", description = "")
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
	@Operation(summary = "create agent", description = "creating agent")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public ResponseEntity<String> createAgent(@RequestBody AgentRequestDto agentRequestDto) {
		System.out.println("inside get All Cities");
		return new ResponseEntity<String>(agentManagementService.createAgent(agentRequestDto), HttpStatus.CREATED);
	}

	@GetMapping("/agents/{agentId}")
	@Operation(summary = "get  agent by id", description = " agent")
	public ResponseEntity<AgentResponseDto> getAgentById(@PathVariable long agentId) {
		return new ResponseEntity<AgentResponseDto>(agentManagementService.getAgentById(agentId), HttpStatus.OK);
	}

	@PutMapping("/agents")
	@Operation(summary = "update agent", description = " agent")
	public ResponseEntity<String> updateAgent(@RequestBody AgentRequestDto agentRequestDto) {
		return new ResponseEntity<String>(agentManagementService.updateAgent(agentRequestDto), HttpStatus.OK);
	}

	@GetMapping("/insurance-setting")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<InsuranceSettingResponseDto> getInsuranceSetting() {
		return new ResponseEntity<InsuranceSettingResponseDto>(settingService.getInsuranceSetting(), HttpStatus.OK);
	}

	@GetMapping("/tax-setting")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<TaxSettingResponseDto> getTaxSetting() {
		return new ResponseEntity<TaxSettingResponseDto>(settingService.getTaxSetting(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@PostMapping("/customer/{documentId}/verify/{employeeId}")
	public ResponseEntity<String> verifyDocument(@PathVariable(name = "documentId") int documentId,
			@PathVariable(name = "employeeId") long employeeId) {

		String response = employeeManagementService.verifyDocument(documentId, employeeId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@PreAuthorize("hasRole('EMPLOYEE')")
	@GetMapping("/customer/queries")
	public ResponseEntity<PagedResponse<QueryResponseDto>> getAllQueries(@RequestParam(name = "page",defaultValue = "5")int page,@RequestParam(name = "size",defaultValue = "5")int size,@RequestParam(name = "sortBy",defaultValue = "queryId")String sortBy,@RequestParam(name = "direction",defaultValue = "asc")String direction){
		return new ResponseEntity<PagedResponse<QueryResponseDto>>(customerManagementService.getAllQueries(page,size,sortBy,direction),HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('EMPLOYEE')")
	@PostMapping("/customer/queries/{queryId}/respond")
	public ResponseEntity<String> respondToQuery(@PathVariable(name = "queryId") long queryId, @RequestBody QueryReplyDto queryReplyDto) {
		String response = customerManagementService.respondToQuery(queryId,queryReplyDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
