package com.helpdesk.ticketing.service;

import com.helpdesk.ticketing.entity.*;
import com.helpdesk.ticketing.enums.Priority;
import com.helpdesk.ticketing.enums.TicketStatus;
import com.helpdesk.ticketing.exception.InvalidStatusTransitionException;
import com.helpdesk.ticketing.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final SlaRuleRepository slaRuleRepository;
    private final TicketStatusLogRepository ticketStatusLogRepository;
    private static final Map<TicketStatus, Set<TicketStatus>> VALID_TRANSITIONS = Map.of(
            TicketStatus.OPEN, Set.of(TicketStatus.IN_PROGRESS),
            TicketStatus.IN_PROGRESS, Set.of(TicketStatus.RESOLVED),
            TicketStatus.RESOLVED, Set.of(TicketStatus.CLOSED, TicketStatus.REOPENED),
            TicketStatus.REOPENED, Set.of(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED),
            TicketStatus.CLOSED, Set.of()
    );

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, AgentRepository agentRepository, SlaRuleRepository slaRuleRepository, TicketStatusLogRepository ticketStatusLogRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
        this.slaRuleRepository = slaRuleRepository;
        this.ticketStatusLogRepository = ticketStatusLogRepository;
    }

    public Ticket createTicket(Ticket ticket, Long userId, Long slaRuleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        SlaRule slaRule = slaRuleRepository.findById(slaRuleId).orElseThrow(() -> new RuntimeException("SLA Rule not found"));

        ticket.setUser(user);
        ticket.setSlaRule(slaRule);

        if (ticket.getStatus() == null) {
            ticket.setStatus(TicketStatus.OPEN);
        }

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public Ticket assignAgent(Long ticketId, Long agentId) {
        Ticket ticket = getTicketById(ticketId);

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new InvalidStatusTransitionException("Cannot assign agent to a closed ticket");
        }

        Agent agent = agentRepository.findById(agentId).orElseThrow(() -> new RuntimeException("Agent not found"));

        ticket.setAgent(agent);
        return ticketRepository.save(ticket);
    }

    public Ticket updateStatus(Long ticketId, TicketStatus newStatus) {

        Ticket ticket = getTicketById(ticketId);

        TicketStatus oldStatus = ticket.getStatus();

        if (!isValidTransition(oldStatus, newStatus)) {
            throw new InvalidStatusTransitionException("Invalid status transition from " + oldStatus + " to " + newStatus);
        }

        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketStatusLog log = new TicketStatusLog();
        log.setTicket(savedTicket);
        log.setFromStatus(oldStatus);
        log.setToStatus(newStatus);
        log.setChangedAt(LocalDateTime.now());

        ticketStatusLogRepository.save(log);

        return savedTicket;
    }

    private boolean isValidTransition(TicketStatus oldStatus, TicketStatus newStatus) {
        return VALID_TRANSITIONS.getOrDefault(oldStatus, Set.of()).contains(newStatus);
    }

    public List<Ticket> getOverdueTickets() {
        LocalDateTime now = LocalDateTime.now();

        return ticketRepository.findAll()
                .stream()
                .filter(ticket -> ticket.getClosedAt() == null)
                .filter(ticket -> ticket.getCreatedAt() != null)
                .filter(ticket -> ticket.getSlaRule() != null)
                .filter(ticket -> ticket.getCreatedAt()
                        .plusHours(ticket.getSlaRule().getTargetHours())
                        .isBefore(now))
                .toList();
    }

    public double getAverageResolutionTimeInHours() {
        List<Ticket> resolvedTickets = ticketRepository.findAll()
                .stream()
                .filter(ticket -> ticket.getResolvedAt() != null)
                .filter(ticket -> ticket.getCreatedAt() != null)
                .toList();

        if (resolvedTickets.isEmpty()) {
            return 0;
        }

        double totalHours = resolvedTickets.stream().mapToDouble(ticket -> java.time.Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt()).toHours()).sum();

        return totalHours / resolvedTickets.size();
    }

    public List<Ticket> getTickets(TicketStatus status, Priority priority, String category, Long assignedTo) {

        if (status != null) {
            return ticketRepository.findByStatus(status);
        }

        if (priority != null) {
            return ticketRepository.findByPriority(priority);
        }

        if (category != null) {
            return ticketRepository.findByCategory(category);
        }

        if (assignedTo != null) {
            return ticketRepository.findByAgentAgentId(assignedTo);
        }

        return ticketRepository.findAll();
    }

    public long getTotalTickets() {
        return ticketRepository.count();
    }

    public long getOpenTickets() {
        return ticketRepository.findByStatus(TicketStatus.OPEN).size();
    }

    public long getClosedTickets() {
        return ticketRepository.findByStatus(TicketStatus.CLOSED).size();
    }
}
