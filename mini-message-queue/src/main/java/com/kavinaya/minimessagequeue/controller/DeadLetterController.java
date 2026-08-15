package com.kavinaya.minimessagequeue.controller;

import com.kavinaya.minimessagequeue.entity.Message;
import com.kavinaya.minimessagequeue.service.DeadLetterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dead-letters")
public class DeadLetterController {

    private final DeadLetterService deadLetterService;

    public DeadLetterController(DeadLetterService deadLetterService) {
        this.deadLetterService = deadLetterService;
    }

    @PostMapping("/{id}/replay")
    public ResponseEntity<Message> replayMessage(
            @PathVariable Long id) {

        Message message = deadLetterService.replayMessage(id);

        return ResponseEntity.ok(message);
    }
}