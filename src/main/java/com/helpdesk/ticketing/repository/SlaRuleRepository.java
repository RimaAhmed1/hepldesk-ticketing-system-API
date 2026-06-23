package com.helpdesk.ticketing.repository;
import com.helpdesk.ticketing.entity.SlaRule;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SlaRuleRepository extends JpaRepository<SlaRule, Long>{
}
