package com.monocept.myapp.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.monocept.myapp.entity.AgentEarnings;
import com.monocept.myapp.repository.AgentEarningsRepository;

@Service
public class AgentEarningsService {

    @Autowired
    private AgentEarningsRepository agentEarningsRepository;

    public Page<AgentEarnings> getAgentEarningsWithFilters(Long agentId, String status, Double minAmount, Double maxAmount, 
                                                           LocalDateTime fromDate, LocalDateTime toDate, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return agentEarningsRepository.findWithFilters(agentId, status, minAmount, maxAmount, fromDate, toDate, pageRequest);
    }
}
