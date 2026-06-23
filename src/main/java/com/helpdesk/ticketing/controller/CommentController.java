package com.helpdesk.ticketing.controller;
import com.helpdesk.ticketing.entity.Comment;
import com.helpdesk.ticketing.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/ticket/{ticketId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@PathVariable Long ticketId, @RequestBody Comment comment) {
        return commentService.addComment(ticketId, comment);
    }

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/ticket/{ticketId}")
    public List<Comment> getCommentsByTicket(@PathVariable Long ticketId) {
        return commentService.getCommentsByTicket(ticketId);
    }
}
