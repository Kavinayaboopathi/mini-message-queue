package com.kavinaya.minimessagequeue.service;

import com.kavinaya.minimessagequeue.entity.DeadLetterMessage;
import com.kavinaya.minimessagequeue.entity.Message;
import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;
import com.kavinaya.minimessagequeue.repository.DeadLetterMessageRepository;
import com.kavinaya.minimessagequeue.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class DeadLetterService {

    private final DeadLetterMessageRepository deadLetterMessageRepository;
    private final MessageRepository messageRepository;

    public DeadLetterService(
            DeadLetterMessageRepository deadLetterMessageRepository,
            MessageRepository messageRepository) {

        this.deadLetterMessageRepository = deadLetterMessageRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Message replayMessage(Long deadLetterId) {

        DeadLetterMessage deadLetterMessage =
                deadLetterMessageRepository.findById(deadLetterId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Dead letter message not found with id: "
                                                + deadLetterId));

        Message message = Message.builder()
                .queueName(deadLetterMessage.getQueueName())
                .messageBody(deadLetterMessage.getMessageBody())
                .status(MessageStatus.PENDING)
                .retryCount(0)
                .build();

        Message savedMessage = messageRepository.save(message);

        deadLetterMessageRepository.delete(deadLetterMessage);

        return savedMessage;
    }
}