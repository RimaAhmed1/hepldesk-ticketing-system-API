package com.helpdesk.ticketing.service;
import com.helpdesk.ticketing.entity.Comment;
import com.helpdesk.ticketing.entity.Ticket;
import com.helpdesk.ticketing.repository.CommentRepository;
import com.helpdesk.ticketing.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    public CommentService(CommentRepository commentRepository,
                          TicketRepository ticketRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
    }

    public Comment addComment(Long ticketId, Comment comment) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        comment.setTicket(ticket);

        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }

        return commentRepository.save(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByTicket(Long ticketId) {
        return commentRepository.findByTicketTicketId(ticketId);
    }
}
