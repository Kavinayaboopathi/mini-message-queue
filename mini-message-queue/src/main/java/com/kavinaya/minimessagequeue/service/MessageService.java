package com.kavinaya.minimessagequeue.service;

import com.kavinaya.minimessagequeue.dto.MessageResponse;
import com.kavinaya.minimessagequeue.dto.PublishMessageRequest;
import com.kavinaya.minimessagequeue.entity.Message;
import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import com.kavinaya.minimessagequeue.mapper.MessageMapper;
import com.kavinaya.minimessagequeue.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import org.springframework.transaction.annotation.Transactional;

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

        return MessageMapper.toResponse(savedMessage);
    }

    @Transactional
    public MessageResponse consumeMessage(String queueName) {

        Message message = messageRepository
                .findFirstByQueueNameAndStatusOrderByCreatedAtAsc(
                        queueName,
                        MessageStatus.PENDING
                )
                .orElseThrow(() ->
                        new NoSuchElementException("No pending messages found in queue: " + queueName)
                );

        message.setStatus(MessageStatus.PROCESSING);
        message.setLockedAt(LocalDateTime.now());

        Message updatedMessage = messageRepository.save(message);

        return MessageMapper.toResponse(updatedMessage);
    }

    public MessageResponse acknowledgeMessage(Long id) {

        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Message not found with id: " + id));

        if (message.getStatus() != MessageStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only PROCESSING messages can be acknowledged");
        }

        message.setStatus(MessageStatus.SUCCESS);

        Message updatedMessage = messageRepository.save(message);

        return MessageMapper.toResponse(updatedMessage);
    }
}