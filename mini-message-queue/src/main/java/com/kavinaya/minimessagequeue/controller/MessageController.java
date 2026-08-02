package com.kavinaya.minimessagequeue.controller;

import com.kavinaya.minimessagequeue.dto.MessageResponse;
import com.kavinaya.minimessagequeue.dto.PublishMessageRequest;
import com.kavinaya.minimessagequeue.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse publishMessage(
            @Valid @RequestBody PublishMessageRequest request) {

        return messageService.publishMessage(request);
    }

    @GetMapping("/consume/{queueName}")
    public MessageResponse consumeMessage(
            @PathVariable String queueName) {

        return messageService.consumeMessage(queueName);
    }

    @PostMapping("/ack/{id}")
    public MessageResponse acknowledgeMessage(
            @PathVariable Long id) {

        return messageService.acknowledgeMessage(id);
    }
}