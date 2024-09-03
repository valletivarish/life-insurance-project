package com.monocept.myapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.CityRequestDto;
import com.monocept.myapp.dto.CityResponseDto;
import com.monocept.myapp.dto.StateRequestDto;
import com.monocept.myapp.dto.StateResponseDto;
import com.monocept.myapp.entity.City;
import com.monocept.myapp.entity.State;
import com.monocept.myapp.exception.StudentApiException;
import com.monocept.myapp.repository.CityRepository;
import com.monocept.myapp.repository.StateRepository;
import com.monocept.myapp.util.PagedResponse;

@Service
public class StateAndCityManagementServiceImpl implements StateAndCityManagementService {

	@Autowired
	private StateRepository stateRepository;
	@Autowired
	private CityRepository cityRepository;

	@Override
	public StateResponseDto createState(StateRequestDto stateRequestDto) {
		State state = convertStateRequestDtoToState(stateRequestDto);
		return convertStateToStateResponseDto(stateRepository.save(state));
	}

	private State convertStateRequestDtoToState(StateRequestDto stateRequestDto) {
		State state = new State();
		state.setStateId(stateRequestDto.getId());
		state.setName(stateRequestDto.getName());
		state.setActive(true);
		return state;
	}

	private StateResponseDto convertStateToStateResponseDto(State state) {
		StateResponseDto stateResponseDto = new StateResponseDto();
		stateResponseDto.setId(state.getStateId());
		stateResponseDto.setName(state.getName());
		stateResponseDto.setActive(state.isActive());
		if (state.getCity() != null) {
			stateResponseDto.setCities(state.getCity().stream().map(city -> convertCityToCityResponseDto(city))
					.collect(Collectors.toList()));
		}
		return stateResponseDto;
	}

	private CityResponseDto convertCityToCityResponseDto(City city) {
		CityResponseDto cityResponseDto = new CityResponseDto();
		if (city.getCityId() != null) {
			cityResponseDto.setId(city.getCityId());
		}
		cityResponseDto.setCity(city.getName());
		cityResponseDto.setActive(city.isActive());
		return cityResponseDto;
	}

	@Override
	public StateResponseDto updateState(StateRequestDto stateRequestDto) {
		State existingState = stateRepository.findById(stateRequestDto.getId()).orElse(null);
		if (existingState == null) {

		}
		if (stateRequestDto.getName() != null && stateRequestDto.getName() != "") {
			existingState.setName(stateRequestDto.getName());
		}
		existingState.setActive(stateRequestDto.isActive());
		return convertStateToStateResponseDto(stateRepository.save(existingState));
	}

	@Override
	public String deactivateState(long id) {
		State existingState = stateRepository.findById(id).orElse(null);
		if (existingState == null) {

		}
		if (!existingState.isActive()) {

		}
		existingState.setActive(false);
		stateRepository.save(existingState);
		return "State deleted successfully";
	}

	@Override
	public StateResponseDto getStateById(long id) {
		State existingState = stateRepository.findById(id).orElse(null);
		if (existingState == null) {

		}
		return convertStateToStateResponseDto(existingState);
	}

	@Override
	public PagedResponse<StateResponseDto> getAllStates(int page, int size, String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		PageRequest pageable = PageRequest.of(page, size, sort);

		Page<State> statePage = stateRepository.findAll(pageable);

		List<StateResponseDto> stateResponseDtos = statePage.getContent().stream()
				.map(state -> convertStateToStateResponseDto(state)).collect(Collectors.toList());

		return new PagedResponse<StateResponseDto>(stateResponseDtos, statePage.getNumber(), statePage.getSize(),
				statePage.getTotalElements(), statePage.getTotalPages(), statePage.isLast());
	}

	@Override
	public CityResponseDto createCity(long stateId, CityRequestDto cityRequestDto) {
		State state = stateRepository.findById(stateId)
				.orElseThrow(() -> new StudentApiException(HttpStatus.NOT_FOUND, "State not found"));
		City city = convertCityRequestDtoToCity(cityRequestDto);
		if(!state.isActive()) {
			throw new StudentApiException(HttpStatus.NOT_FOUND, "state is not active");
		}
		city.setState(state);
		City createdCity = cityRepository.save(city);
		CityResponseDto responseDto = convertCityToCityResponseDto(createdCity);
		responseDto.setState(state.getName());
		return responseDto;
	}

	private City convertCityRequestDtoToCity(CityRequestDto cityRequestDto) {
		City city = new City();
		city.setName(cityRequestDto.getName());
		city.setActive(cityRequestDto.isActive());
		return city;
	}

	@Override
	public List<CityResponseDto> getAllCitiesByStateId(long stateId) {
		State state = stateRepository.findById(stateId)
				.orElseThrow(() -> new StudentApiException(HttpStatus.NOT_FOUND, "state not found"));
		List<CityResponseDto> cities = state.getCity().stream().map(c -> convertCityToCityResponseDto(c))
				.collect(Collectors.toList());

		return cities;
	}

	@Override
	public CityResponseDto updateCity(Long stateId, CityRequestDto cityRequestDto) {
		State state = stateRepository.findById(stateId)
				.orElseThrow(() -> new StudentApiException(HttpStatus.NOT_FOUND, "State not found"));
		City existingCity = cityRepository.findById(cityRequestDto.getId())
				.orElseThrow(() -> new StudentApiException(HttpStatus.NOT_FOUND, "City is not found"));
		if (cityRequestDto.getName() != null && cityRequestDto.getName() != "") {
			existingCity.setName(cityRequestDto.getName());
		}
		existingCity.setState(state);
		CityResponseDto responseDto = convertCityToCityResponseDto(existingCity);
		responseDto.setState(state.getName());
		return responseDto;
	}

	@Override
	public String deactivateCity(Long cityId) {
		City existingCity = cityRepository.findById(cityId)
				.orElseThrow(() -> new StudentApiException(HttpStatus.NOT_FOUND, "City not found"));
		if (!existingCity.isActive()) {
			throw new StudentApiException(HttpStatus.CONFLICT, "City is already deleted");
		}
		existingCity.setActive(false);
		cityRepository.save(existingCity);
		return "City deleted successfully";
	}

	@Override
	public CityResponseDto getCityById(Long cityId) {
		City city = cityRepository.findById(cityId)
				.orElseThrow(() -> new StudentApiException(HttpStatus.NOT_FOUND, "city not found"));
		CityResponseDto responseDto = convertCityToCityResponseDto(city);
		responseDto.setCity(city.getState().getName());
		return responseDto;
	}

}
