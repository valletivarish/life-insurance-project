package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monocept.myapp.entity.Agent;
 
public interface AgentRepository extends JpaRepository<Agent, Long> {

}
