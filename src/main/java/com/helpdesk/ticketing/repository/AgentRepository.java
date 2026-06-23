package com.helpdesk.ticketing.repository;
import com.helpdesk.ticketing.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AgentRepository extends JpaRepository<Agent, Long>{
}
