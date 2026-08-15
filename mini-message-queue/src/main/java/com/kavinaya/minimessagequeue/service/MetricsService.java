package com.kavinaya.minimessagequeue.service;

import com.kavinaya.minimessagequeue.dto.MetricsResponse;
import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;
import com.kavinaya.minimessagequeue.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final MessageRepository messageRepository;

    public MetricsService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MetricsResponse getMetrics() {

        long pending =
                messageRepository.countByStatus(MessageStatus.PENDING);

        long processing =
                messageRepository.countByStatus(MessageStatus.PROCESSING);

        long success =
                messageRepository.countByStatus(MessageStatus.SUCCESS);

        long dead =
                messageRepository.countByStatus(MessageStatus.DEAD);

        return new MetricsResponse(
                pending,
                processing,
                success,
                dead
        );
    }
}