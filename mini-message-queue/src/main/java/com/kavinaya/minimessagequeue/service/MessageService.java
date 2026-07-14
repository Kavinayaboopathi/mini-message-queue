package com.kavinaya.minimessagequeue.service;

import com.kavinaya.minimessagequeue.dto.MessageResponse;
import com.kavinaya.minimessagequeue.dto.PublishMessageRequest;
import com.kavinaya.minimessagequeue.entity.Message;
import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;
import com.kavinaya.minimessagequeue.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MessageResponse publishMessage(PublishMessageRequest request) {

        Message message = new Message();

        message.setQueueName(request.getQueueName());
        message.setMessageBody(request.getMessageBody());
        message.setStatus(MessageStatus.PENDING);
        message.setRetryCount(0);

        Message savedMessage = messageRepository.save(message);

        MessageResponse response = new MessageResponse();

        response.setId(savedMessage.getId());
        response.setQueueName(savedMessage.getQueueName());
        response.setMessageBody(savedMessage.getMessageBody());
        response.setStatus(savedMessage.getStatus());
        response.setRetryCount(savedMessage.getRetryCount());
        response.setCreatedAt(savedMessage.getCreatedAt());

        return response;
    }
}