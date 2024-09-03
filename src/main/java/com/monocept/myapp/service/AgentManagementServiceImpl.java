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

import com.monocept.myapp.dto.AgentRequestDto;
import com.monocept.myapp.dto.AgentResponseDto;
import com.monocept.myapp.entity.Address;
import com.monocept.myapp.entity.Agent;
import com.monocept.myapp.entity.City;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.State;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException.UserNotFoundException;
import com.monocept.myapp.repository.AddressRepository;
import com.monocept.myapp.repository.AgentRepository;
import com.monocept.myapp.repository.CityRepository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.StateRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.util.PagedResponse;

@Service
public class AgentManagementServiceImpl implements AgentManagementService {

	@Autowired
	private AgentRepository agentRepository;

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
	
<<<<<<< HEAD
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public String createAgent(AgentRequestDto agentRequestDto) {
		if (userRepository.existsByUsername(agentRequestDto.getUsername())) {
            throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Username already exists!");
        }

        if (userRepository.existsByEmail(agentRequestDto.getEmail())) {
            throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Email already exists!");
        }
=======
	
	@Autowired
	private RoleRepository roleRepository;


	@Override
	public String createAgent(AgentRequestDto agentRequestDto) {

		if (userRepository.existsByUsername(agentRequestDto.getUsername())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Username already exists!");
		}

		if (userRepository.existsByEmail(agentRequestDto.getEmail())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Email already exists!");
		}
>>>>>>> 5a943960109660767deb682f984be959c9f159ff
		Agent agent = new Agent();
		User user = new User();
		user.setEmail(agentRequestDto.getEmail());
		user.setUsername(agentRequestDto.getUsername());
		user.setPassword(passwordEncoder.encode(agentRequestDto.getPassword()));
		Set<Role> roles = new HashSet<>();
		String roleName="ROLE_AGENT";
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));
		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);
		Address address = new Address();
		State state = stateRepository.findById(agentRequestDto.getStateId()).orElse(null);
		List<City> cities = state.getCity();
		City city = cities.stream().filter(c -> c.getCityId() == agentRequestDto.getCityId()).findFirst().orElse(null);
		address.setCity(city);
		agent.setAddress(address);

		address.setHouseNo(agentRequestDto.getHouseNo());
		address.setPincode(agentRequestDto.getPincode());
		address.setApartment(agentRequestDto.getApartment());

		addressRepository.save(address);
		agent.setAddress(address);
		agent.setAgentId(agentRequestDto.getAgentId());
		agent.setFirstName(agentRequestDto.getFirstName());
		agent.setLastName(agentRequestDto.getLastName());
<<<<<<< HEAD
=======

>>>>>>> 5a943960109660767deb682f984be959c9f159ff
		agent.setUser(user);
		agentRepository.save(agent);

		System.out.println("hello");
		return "Agent Created Successfully";

	}

	@Override
	public PagedResponse<AgentResponseDto> getAllAgents(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<Agent> agentPage = agentRepository.findAll(pageRequest);
		List<AgentResponseDto> agents = agentPage.getContent().stream()
				.map(agent -> convertAgentToAgentResponseDto(agent)).collect(Collectors.toList());
		return new PagedResponse<AgentResponseDto>(agents, agentPage.getNumber(), agentPage.getSize(),
				agentPage.getTotalElements(), agentPage.getTotalPages(), agentPage.isLast());
	}

	private AgentResponseDto convertAgentToAgentResponseDto(Agent agent) {
		AgentResponseDto agentResponseDto = new AgentResponseDto();
		agentResponseDto.setFirstName(agent.getFirstName());
		agentResponseDto.setLastName(agent.getLastName());
		agentResponseDto.setAgentId(agent.getAgentId());
		Address address = agent.getAddress();
		User user = agent.getUser();
		agentResponseDto.setFirstName(agent.getFirstName());
		agentResponseDto.setLastName(agent.getLastName());
		agentResponseDto.setApartment(address.getApartment());
		agentResponseDto.setHouseNo(address.getHouseNo());
		agentResponseDto.setPincode(address.getPincode());
		agentResponseDto.setUsername(user.getUsername());
		agentResponseDto.setEmail(user.getEmail());
		City city = address.getCity();
		agentResponseDto.setCity(city.getName());
		State state = city.getState();
		agentResponseDto.setState(state.getName());
		return agentResponseDto;
	}

	@Override
	public String updateAgent(AgentRequestDto agentRequestDto) {
		Agent existingAgent = agentRepository.findById(agentRequestDto.getAgentId()).orElseThrow(
				() -> new IllegalArgumentException("Agent not found with ID: " + agentRequestDto.getAgentId()));

		existingAgent.setFirstName(agentRequestDto.getFirstName());
		existingAgent.setLastName(agentRequestDto.getLastName());

		Address existingAddress = existingAgent.getAddress();
		existingAddress.setApartment(agentRequestDto.getApartment());
		existingAddress.setHouseNo(agentRequestDto.getHouseNo());
		existingAddress.setPincode(agentRequestDto.getPincode());
		City city = cityRepository.findById(agentRequestDto.getCityId()).orElse(null);
		City newCity = addressRepository.findByCity(city);
		existingAddress.setCity(newCity);

		addressRepository.save(existingAddress);

		User existingUser = existingAgent.getUser();
		existingUser.setUsername(agentRequestDto.getUsername());
		existingUser.setEmail(agentRequestDto.getEmail());

		userRepository.save(existingUser);

		agentRepository.save(existingAgent);

		return "Agent updated successfully";

	}

	@Override
	public AgentResponseDto getAgentById(long agentId) {
		Agent agent = agentRepository.findById(agentId).orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Agent Not found"));
		return convertAgentToAgentResponseDto(agent);
	}

	@Override
	public String deleteAgent(long id) {
		Agent agent = agentRepository.findById(id)
				.orElseThrow(() -> new GuardianLifeAssuranceApiException(HttpStatus.NOT_FOUND, "Agent Not found"));
		agent.setActive(false);
		agentRepository.save(agent);
		return "Agent deleted Successfully";
	}

}
