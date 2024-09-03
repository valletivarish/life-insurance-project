package com.monocept.myapp.service;

import com.monocept.myapp.dto.AgentRequestDto;
import com.monocept.myapp.dto.AgentResponseDto;
import com.monocept.myapp.util.PagedResponse;

public interface AgentManagementService {

	String createAgent(AgentRequestDto agentRequestDto);

	PagedResponse<AgentResponseDto> getAllAgents(int page, int size, String sortBy, String direction);

	String updateAgent(AgentResponseDto agentResponseDto);

}
