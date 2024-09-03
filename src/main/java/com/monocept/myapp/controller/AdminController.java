package com.monocept.myapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.monocept.myapp.dto.CityRequestDto;
import com.monocept.myapp.dto.CityResponseDto;
import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.dto.StateRequestDto;
import com.monocept.myapp.dto.StateResponseDto;
import com.monocept.myapp.dto.TaxSettingRequestDto;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.EmployeeManagementService;
import com.monocept.myapp.service.SettingService;
import com.monocept.myapp.service.StateAndCityManagementService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/GuardianLifeAssurance/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	@Autowired
	private StateAndCityManagementService stateAndCityManagementService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private EmployeeManagementService employeeManagementService;
	
	@Autowired
	private AgentManagementService agentManagementService;
	
	
	@PostMapping("/states")
	@Operation(summary = "Create a new state", description = "Add a new state to the system")
	public ResponseEntity<StateResponseDto> createState(@RequestBody StateRequestDto stateRequestDto) {
		return new ResponseEntity<StateResponseDto>(stateAndCityManagementService.createState(stateRequestDto),
				HttpStatus.ACCEPTED);
	}

	@PutMapping("/states")
	@Operation(summary = "Update state details", description = "Update the details of an existing state")
	public ResponseEntity<StateResponseDto> updateState(@RequestBody StateRequestDto stateRequestDto) {
		return new ResponseEntity<StateResponseDto>(stateAndCityManagementService.updateState(stateRequestDto),
				HttpStatus.OK);
	}

	@DeleteMapping("/states/{stateId}")
	@Operation(summary = "Deactivate a state", description = "Mark a state as inactive by its ID")
	public ResponseEntity<String> deactivateState(@PathVariable(name = "stateId") long id) {
		return new ResponseEntity<String>(stateAndCityManagementService.deactivateState(id), HttpStatus.OK);
	}

	@GetMapping("states/{stateId}")
	@Operation(summary = "Get state by ID", description = "Retrieve a specific state by its ID")
	public ResponseEntity<StateResponseDto> getStateById(@PathVariable(name = "stateId") long id) {
		return new ResponseEntity<StateResponseDto>(stateAndCityManagementService.getStateById(id), HttpStatus.OK);
	}

	@GetMapping("states")
	@Operation(summary = "Get all states", description = "Retrieve a paginated list of all states")
	public ResponseEntity<PagedResponse<StateResponseDto>> getAllStates(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "stateId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<StateResponseDto>>(
				stateAndCityManagementService.getAllStates(page, size, sortBy, direction), HttpStatus.OK);

	}

	@PostMapping("states/{stateId}/cities")
	@Operation(summary = "Create a new city", description = "Add a new city to a specific state")
	public ResponseEntity<CityResponseDto> createCity(@PathVariable(name = "stateId") long stateId,
			@RequestBody CityRequestDto cityRequestDto) {
		return new ResponseEntity<CityResponseDto>(stateAndCityManagementService.createCity(stateId, cityRequestDto),
				HttpStatus.CREATED);
	}

	@PutMapping("states/{stateId}/cities")
	@Operation(summary = "Update city details", description = "Update the details of an existing city")
	public ResponseEntity<CityResponseDto> updateCity(@PathVariable Long stateId,
			@RequestBody CityRequestDto cityRequestDto) {
		return new ResponseEntity<CityResponseDto>(stateAndCityManagementService.updateCity(stateId, cityRequestDto),
				HttpStatus.OK);
	}

	@DeleteMapping("states/{stateId}/cities/{cityId}")
	@Operation(summary = "Deactivate a city", description = "Mark a city as inactive by its ID")
	public ResponseEntity<String> deactivateCity(@PathVariable Long cityId) {
		return new ResponseEntity<String>(stateAndCityManagementService.deactivateCity(cityId), HttpStatus.OK);
	}

	@GetMapping("/states/{stateId}/cities")
	@Operation(summary = "Get all cities for a state", description = "Retrieve all cities for a specific state by the state ID")
	public ResponseEntity<List<CityResponseDto>> getAllCities(@PathVariable long stateId) {
		return new ResponseEntity<List<CityResponseDto>>(stateAndCityManagementService.getAllCitiesByStateId(stateId),
				HttpStatus.OK);
	}

	@GetMapping("/states/{stateId}/cities/{cityId}")
	@Operation(summary = "Get city by ID", description = "Retrieve a specific city by its ID")
	public ResponseEntity<CityResponseDto> getCityById(@PathVariable Long stateId, @PathVariable Long cityId) {
		return new ResponseEntity<CityResponseDto>(stateAndCityManagementService.getCityById(cityId), HttpStatus.OK);
	}

	@PostMapping("/taxes")
	@Operation(summary = "Create a new tax setting", description = "Add a new tax setting to the system")
	public ResponseEntity<String> createTaxSetting(@RequestBody TaxSettingRequestDto taxSettingRequestDto) {
		return new ResponseEntity<String>(settingService.createTaxSetting(taxSettingRequestDto), HttpStatus.CREATED);
	}
	@GetMapping("/employees")
	@Operation(summary = "Get all employees", description = "Retrieve a paginated list of all employees")
	public ResponseEntity<PagedResponse<EmployeeResponseDto>> getAllEmployees(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "employeeId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<EmployeeResponseDto>>(
				employeeManagementService.getAllEmployees(page, size, sortBy, direction), HttpStatus.OK);
	}

	@PostMapping("/employees")
	@Operation(summary = "Create a new employee", description = "Add a new employee to the system")
	public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
		return new ResponseEntity<String>(employeeManagementService.createEmployee(employeeRequestDto),
				HttpStatus.CREATED);

	}

	@PutMapping("/employees")
	@Operation(summary = "Update employee details", description = "Update the details of an existing employee")
	public ResponseEntity<EmployeeResponseDto> updateEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
		return new ResponseEntity<EmployeeResponseDto>(employeeManagementService.updateEmployee(employeeRequestDto),
				HttpStatus.OK);
	}
	@DeleteMapping("/employees/{employeeId}")
	@Operation(summary = "Deactivate an employee", description = "Mark an employee as inactive by their ID")
	public ResponseEntity<String> deactivateEmployee(@PathVariable(name = "employeeId") long employeeId){
		return new ResponseEntity<String>(employeeManagementService.deactivateEmployee(employeeId),HttpStatus.OK);
	}
	
	@PostMapping("/agents")
	public ResponseEntity<String> createAgent(@RequestBody AgentRequestDto agentRequestDto) {
		return new ResponseEntity<String>(agentManagementService.createAgent(agentRequestDto),HttpStatus.CREATED);
	}
	@GetMapping("/agents")
	public ResponseEntity<PagedResponse<AgentResponseDto>> getAllAgents(
			@RequestParam(name = "page",defaultValue = "0")int page,@RequestParam(name = "size",defaultValue = "5")int size,@RequestParam(name = "sortBy",defaultValue = "agentId") String sortBy,@RequestParam(name = "direction") String direction) {
		return new ResponseEntity<PagedResponse<AgentResponseDto>>(agentManagementService.getAllAgents(page,size,sortBy,direction),HttpStatus.OK);
	}
	@PutMapping("/agents")
	public ResponseEntity<String> updateAgent(@RequestBody AgentResponseDto agentResponseDto) {		
		return new ResponseEntity<String>(agentManagementService.updateAgent(agentResponseDto),HttpStatus.OK);
	}
	
	
}
