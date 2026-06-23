package com.helpdesk.ticketing.repository;
import com.helpdesk.ticketing.entity.TicketStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketStatusLogRepository extends JpaRepository<TicketStatusLog, Long> {
}
