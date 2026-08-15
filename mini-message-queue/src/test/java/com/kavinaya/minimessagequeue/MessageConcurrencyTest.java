package com.kavinaya.minimessagequeue;

import com.kavinaya.minimessagequeue.entity.Message;
import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;
import com.kavinaya.minimessagequeue.repository.MessageRepository;
import com.kavinaya.minimessagequeue.service.MessageService;
import com.kavinaya.minimessagequeue.dto.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageConcurrencyTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    void setup() {

        messageRepository.deleteAll();

        Message message1 = Message.builder()
                .queueName("test-queue")
                .messageBody("Message 1")
                .status(MessageStatus.PENDING)
                .retryCount(0)
                .build();

        Message message2 = Message.builder()
                .queueName("test-queue")
                .messageBody("Message 2")
                .status(MessageStatus.PENDING)
                .retryCount(0)
                .build();

        messageRepository.save(message1);
        messageRepository.save(message2);
    }

    @Test
    void twoConsumersShouldGetDifferentMessages()
            throws Exception {

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        Callable<MessageResponse> consumer1 =
                () -> messageService.consumeMessage("test-queue");

        Callable<MessageResponse> consumer2 =
                () -> messageService.consumeMessage("test-queue");

        Future<MessageResponse> result1 =
                executor.submit(consumer1);

        Future<MessageResponse> result2 =
                executor.submit(consumer2);

        MessageResponse response1 =
                result1.get(5, TimeUnit.SECONDS);

        MessageResponse response2 =
                result2.get(5, TimeUnit.SECONDS);

        executor.shutdown();

        assertNotEquals(
                response1.getId(),
                response2.getId()
        );
    }
}
