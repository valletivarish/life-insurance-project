package com.monocept.myapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.dto.AdminRequestDto;
import com.monocept.myapp.dto.AdminResponseDto;
import com.monocept.myapp.dto.ChangePasswordRequestDto;
import com.monocept.myapp.dto.CityRequestDto;
import com.monocept.myapp.dto.CityResponseDto;
import com.monocept.myapp.dto.ClaimResponseDto;
import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.dto.InsurancePlanRequestDto;
import com.monocept.myapp.dto.InsurancePlanResponseDto;
import com.monocept.myapp.dto.InsuranceSchemeRequestDto;
import com.monocept.myapp.dto.InsuranceSchemeResponseDto;
import com.monocept.myapp.dto.InsuranceSettingRequestDto;
import com.monocept.myapp.dto.PaymentResponseDto;
import com.monocept.myapp.dto.StateRequestDto;
import com.monocept.myapp.dto.StateResponseDto;
import com.monocept.myapp.dto.TaxSettingRequestDto;
import com.monocept.myapp.enums.ClaimStatus;
import com.monocept.myapp.service.AdminService;
import com.monocept.myapp.service.AgentManagementService;
import com.monocept.myapp.service.AuthService;
import com.monocept.myapp.service.ClaimService;
import com.monocept.myapp.service.EmployeeManagementService;
import com.monocept.myapp.service.InsuranceManagementService;
import com.monocept.myapp.service.PaymentService;
import com.monocept.myapp.service.SettingService;
import com.monocept.myapp.service.StateAndCityManagementService;
import com.monocept.myapp.service.WithdrawalService;
import com.monocept.myapp.util.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

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

	@Autowired
	private InsuranceManagementService insuranceManagementService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ClaimService claimService;

	@Autowired
	private AuthService authService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private WithdrawalService withdrawalService;

	@PutMapping("/withdrawal/approve/{withdrawalId}")
	public ResponseEntity<String> approveWithdrawalRequest(@PathVariable long withdrawalId) {
		withdrawalService.approveWithdrawal(withdrawalId);
		return ResponseEntity.ok("Withdrawal request approved and refund processed successfully.");
	}

	@PutMapping("/withdrawal/reject/{withdrawalId}")
	public ResponseEntity<String> rejectWithdrawalRequest(@PathVariable long withdrawalId) {
		withdrawalService.rejectWithdrawal(withdrawalId);
		return ResponseEntity.ok("Withdrawal request rejected successfully.");
	}

	@PutMapping("change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		return new ResponseEntity<String>(authService.changePassword(changePasswordRequestDto), HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<String> updateAdmin(@RequestBody @Valid AdminRequestDto adminRequestDto) {
		return new ResponseEntity<>(adminService.updateAdmin(adminRequestDto), HttpStatus.OK);
	}

	@DeleteMapping("/{adminId}")
	public ResponseEntity<String> deleteAdmin(@PathVariable long adminId) {
		return new ResponseEntity<>(adminService.deleteAdmin(adminId), HttpStatus.OK);
	}

	@GetMapping("/{adminId}")
	public ResponseEntity<AdminResponseDto> getAdmin(@PathVariable long adminId) {
		return new ResponseEntity<>(adminService.getAdmin(adminId), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<String> addAdmin(@RequestBody @Valid AdminRequestDto adminRequestDto) {
		return new ResponseEntity<String>(adminService.addAdmin(adminRequestDto), HttpStatus.OK);
	}

	@PutMapping("/activate/{adminId}")
	public ResponseEntity<String> activateAdmin(@PathVariable(name = "adminId") long adminId) {
		return new ResponseEntity<String>(adminService.activateAdmin(adminId), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<PagedResponse<AdminResponseDto>> getAllAdmin(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "adminId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<PagedResponse<AdminResponseDto>>(
				adminService.getAllAdmin(page, size, sortBy, direction), HttpStatus.OK);
	}

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

	@PutMapping("states/activate/{stateId}")
	@Operation(summary = "activate state by ID", description = "activate a specific state by its ID")
	public ResponseEntity<String> activateStateById(@PathVariable(name = "stateId") long id) {
		return new ResponseEntity<String>(stateAndCityManagementService.activateStateById(id), HttpStatus.OK);
	}

	@PutMapping("states/{stateId}/cities/activate/{cityId}")
	@Operation(summary = "activate city by ID", description = "activate a specific city by its ID")
	public ResponseEntity<String> activateCityById(@PathVariable(name = "stateId") long stateId,
			@PathVariable(name = "cityId") long cityId) {
		return new ResponseEntity<String>(stateAndCityManagementService.activateCityById(stateId, cityId),
				HttpStatus.OK);
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
	public ResponseEntity<String> deactivateCity(@PathVariable long cityId) {
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

	@DeleteMapping("/employees/{employeeId}")
	@Operation(summary = "Deactivate an employee", description = "Mark an employee as inactive by their ID")
	public ResponseEntity<String> deactivateEmployee(@PathVariable(name = "employeeId") long employeeId) {
		return new ResponseEntity<String>(employeeManagementService.deactivateEmployee(employeeId), HttpStatus.OK);
	}

	@DeleteMapping("/agents/{id}")
	@Operation(summary = "Delete an agent", description = "Remove an agent from the system by their ID")
	public ResponseEntity<String> deleteAgent(@PathVariable(name = "id") long id) {
		return new ResponseEntity<String>(agentManagementService.deleteAgent(id), HttpStatus.OK);
	}

	@PostMapping("/insurance-plans")
	@Operation(summary = "Create a new insurance plan", description = "Add a new insurance plan to the system")
	public ResponseEntity<String> createInsurancePlan(@RequestBody InsurancePlanRequestDto insurancePlanRequestDto) {
		return new ResponseEntity<>(insuranceManagementService.createInsurancePlan(insurancePlanRequestDto),
				HttpStatus.CREATED);
	}

	@PutMapping("/insurance-plans")
	@Operation(summary = "Update insurance plan details", description = "Update the details of an existing insurance plan")
	public ResponseEntity<String> updateInsurancePlan(@RequestBody InsurancePlanRequestDto insurancePlanRequestDto) {
		return new ResponseEntity<>(insuranceManagementService.updateInsurancePlan(insurancePlanRequestDto),
				HttpStatus.OK);
	}

	@GetMapping("/insurance-plans")
	@Operation(summary = "Get all insurance plans", description = "Retrieve a paginated list of all insurance plans")
	public ResponseEntity<PagedResponse<InsurancePlanResponseDto>> getAllInsurancePlans(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "InsurancePlanId") String sortBy,
			@RequestParam(name = "direction", defaultValue = "asc") String direction) {
		return new ResponseEntity<>(insuranceManagementService.getAllInsurancePlans(page, size, sortBy, direction),
				HttpStatus.OK);
	}

	@DeleteMapping("/insurance-plans/{insurancePlanId}")
	@Operation(summary = "Deactivate an insurance plan", description = "Mark an insurance plan as inactive by its ID")
	public ResponseEntity<String> deactivateInsurancePlan(
			@PathVariable(name = "insurancePlanId") long insurancePlanId) {
		return new ResponseEntity<>(insuranceManagementService.deactivateInsurancePlan(insurancePlanId), HttpStatus.OK);
	}

	@PostMapping("/insurance-plans/{insurancePlanId}/insurance-scheme")
	@Operation(summary = "Create a new insurance scheme", description = "Add a new insurance scheme to an existing insurance plan")
	public ResponseEntity<String> createInsuranceScheme(@PathVariable(name = "insurancePlanId") long insurancePlanId,
			@RequestParam(name = "schemeImage") MultipartFile multipartFile,
			@ModelAttribute InsuranceSchemeRequestDto requestDto) throws IOException {
		return new ResponseEntity<String>(
				insuranceManagementService.createInsuranceScheme(insurancePlanId, multipartFile, requestDto),
				HttpStatus.CREATED);
	}

	@GetMapping("/insurance-plans/{insurancePlanId}/insurance-scheme")
	@Operation(summary = "Get all insurance schemes", description = "Retrieve all insurance schemes for a specific insurance plan")
	public ResponseEntity<List<InsuranceSchemeResponseDto>> getAllSchemes(
			@PathVariable(name = "insurancePlanId") long insurancePlanId) {
		return new ResponseEntity<List<InsuranceSchemeResponseDto>>(
				insuranceManagementService.getAllInsuranceSchemes(insurancePlanId), HttpStatus.OK);
	}

	@PutMapping("/insurance-plans/{insurancePlanId}/insurance-scheme")
	@Operation(summary = "Update insurance scheme details", description = "Update the details of an existing insurance scheme")
	public ResponseEntity<String> updateInsuranceScheme(@PathVariable(name = "insurancePlanId") long insurancePlanId,
			@RequestParam(name = "schemeImage") MultipartFile multipartFile,
			@ModelAttribute InsuranceSchemeRequestDto requestDto) throws IOException {
		return new ResponseEntity<String>(
				insuranceManagementService.updateInsuranceScheme(insurancePlanId, multipartFile, requestDto),
				HttpStatus.OK);
	}

	@DeleteMapping("/insurance-plans/{insurancePlanId}/insurance-scheme/{insuranceSchemeId}")
	@Operation(summary = "Deactivate an insurance scheme", description = "Mark an insurance scheme as inactive by its ID")
	public ResponseEntity<String> deleteInsuranceScheme(@PathVariable(name = "insurancePlanId") long insurancePlanId,
			@PathVariable(name = "insuranceSchemeId") long insuranceSchemeId) {
		return new ResponseEntity<String>(
				insuranceManagementService.deleteInsuranceScheme(insurancePlanId, insuranceSchemeId), HttpStatus.OK);

	}

	@GetMapping("/insurance-plans/{insurancePlanId}/insurance-scheme/{insuranceSchemeId}")
	@Operation(summary = "Get insurance scheme by ID", description = "Retrieve a specific insurance scheme by its ID")
	public ResponseEntity<InsuranceSchemeResponseDto> getInsuranceSchemeById(
			@PathVariable(name = "insurancePlanId") long insurancePlanId,
			@PathVariable(name = "insuranceSchemeId") long insuranceSchemeId) {
		return new ResponseEntity<InsuranceSchemeResponseDto>(
				insuranceManagementService.getInsuranceById(insurancePlanId, insuranceSchemeId), HttpStatus.OK);

	}

	@PostMapping("/insurance-setting")
	@Operation(summary = "Create a new insurance setting", description = "Add a new insurance setting to the system")
	public ResponseEntity<String> createInsuranceSetting(
			@RequestBody InsuranceSettingRequestDto insuranceSettingRequestDto) {
		return new ResponseEntity<String>(settingService.createInsuranceSetting(insuranceSettingRequestDto),
				HttpStatus.CREATED);

	}

	@PutMapping("/{claimId}/approve")
    public ResponseEntity<String> approveClaim(@PathVariable Long claimId) {
        String response = claimService.approveClaim(claimId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{claimId}/reject")
    public ResponseEntity<String> rejectClaim(@PathVariable Long claimId) {
        String response = claimService.rejectClaim(claimId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

	@GetMapping("/payments")
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	@Operation(summary = "Get all payments with pagination and filtering", description = "Retrieve all payments with pagination, sorting, and optional search filters")
	public ResponseEntity<PagedResponse<PaymentResponseDto>> getAllPayments(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size,
			@RequestParam(name = "sortBy", defaultValue = "paymentDate") String sortBy,
			@RequestParam(name = "direction", defaultValue = "ASC") String direction,
			@RequestParam(name = "minAmount", required = false) Double minAmount,
			@RequestParam(name = "maxAmount", required = false) Double maxAmount,
			@RequestParam(name = "startDate", required = false) LocalDateTime startDate,
			@RequestParam(name = "endDate", required = false) LocalDateTime endDate,
			@RequestParam(name = "customerId", required = false) String customerId,
			@RequestParam(name = "paymentId", required = false) Long paymentId) {

		PagedResponse<PaymentResponseDto> payments = paymentService.getAllPaymentsWithFilters(page, size, sortBy,
				direction, minAmount, maxAmount, startDate, endDate, customerId, paymentId);

		return new ResponseEntity<>(payments, HttpStatus.OK);
	}
	
	@GetMapping("/claims")
    @Operation(summary = "View all claims", description = "Retrieve all claims with pagination, sorting, and optional filters")
    public ResponseEntity<PagedResponse<ClaimResponseDto>> getAllClaims(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "claimId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "status", required = false) ClaimStatus status,
            @RequestParam(name = "customerId", required = false) Long customerId,
            @RequestParam(name = "policyNo", required = false) Long policyNo) {

        PagedResponse<ClaimResponseDto> claims = claimService.getAllClaimsWithFilters(page, size, sortBy, direction, status, customerId, policyNo);
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }

}
