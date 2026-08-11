package com.kavinaya.minimessagequeue.scheduler;

import com.kavinaya.minimessagequeue.entity.DeadLetterMessage;
import com.kavinaya.minimessagequeue.entity.Message;
import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;
import com.kavinaya.minimessagequeue.repository.DeadLetterMessageRepository;
import com.kavinaya.minimessagequeue.repository.MessageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RetryScheduler {

    private final MessageRepository messageRepository;
    private final DeadLetterMessageRepository deadLetterMessageRepository;

    public RetryScheduler(
            MessageRepository messageRepository,
            DeadLetterMessageRepository deadLetterMessageRepository) {

        this.messageRepository = messageRepository;
        this.deadLetterMessageRepository = deadLetterMessageRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void retryMessages() {

        LocalDateTime timeout =
                LocalDateTime.now().minusSeconds(30);

        List<Message> processingMessages =
                messageRepository.findByStatusAndLockedAtBefore(
                        MessageStatus.PROCESSING,
                        timeout
                );

        for (Message message : processingMessages) {

            if (message.getRetryCount() < 3) {

                message.setRetryCount(
                        message.getRetryCount() + 1
                );

                message.setStatus(MessageStatus.PENDING);

                message.setLockedAt(null);

                messageRepository.save(message);

            } else {

                moveToDeadLetterQueue(message);
            }
        }
    }

    private void moveToDeadLetterQueue(Message message) {

        DeadLetterMessage deadLetterMessage =
                DeadLetterMessage.builder()
                        .originalMessageId(message.getId())
                        .queueName(message.getQueueName())
                        .messageBody(message.getMessageBody())
                        .originalStatus(message.getStatus())
                        .retryCount(message.getRetryCount())
                        .reason("Message exceeded maximum retry count")
                        .failedAt(LocalDateTime.now())
                        .build();

        deadLetterMessageRepository.save(deadLetterMessage);

        message.setStatus(MessageStatus.DEAD);
        messageRepository.save(message);
    }
}