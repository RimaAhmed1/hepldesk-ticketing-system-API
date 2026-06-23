package com.helpdesk.ticketing.controller;
import com.helpdesk.ticketing.entity.Ticket;
import com.helpdesk.ticketing.enums.Priority;
import com.helpdesk.ticketing.enums.TicketStatus;
import com.helpdesk.ticketing.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket createTicket(@RequestParam Long userId, @RequestParam Long slaRuleId, @RequestBody Ticket ticket) {
        return ticketService.createTicket(ticket, userId, slaRuleId);
    }

    @GetMapping
    public List<Ticket> getTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long assignedTo) {

        return ticketService.getTickets(status, priority, category, assignedTo);
    }

    @GetMapping("/{ticketId}")
    public Ticket getTicketById(@PathVariable Long ticketId) {
        return ticketService.getTicketById(ticketId);
    }

    @PostMapping("/{ticketId}/assign")
    public Ticket assignAgent(@PathVariable Long ticketId, @RequestParam Long agentId) {
        return ticketService.assignAgent(ticketId, agentId);
    }
    @PutMapping("/{ticketId}/status")
    public Ticket updateStatus(
            @PathVariable Long ticketId,
            @RequestParam TicketStatus status) {

        return ticketService.updateStatus(ticketId, status);
    }
    @GetMapping("/overdue")
    public List<Ticket> getOverdueTickets() {
        return ticketService.getOverdueTickets();
    }

    @GetMapping("/metrics/avg-resolution-time")
    public double getAverageResolutionTime() {
        return ticketService.getAverageResolutionTimeInHours();
    }
    @GetMapping("/dashboard/total")
    public long totalTickets() {
        return ticketService.getTotalTickets();
    }

    @GetMapping("/dashboard/open")
    public long openTickets() {
        return ticketService.getOpenTickets();
    }

    @GetMapping("/dashboard/closed")
    public long closedTickets() {
        return ticketService.getClosedTickets();
    }
}
