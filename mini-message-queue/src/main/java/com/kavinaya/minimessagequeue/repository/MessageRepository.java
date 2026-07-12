package com.kavinaya.minimessagequeue.repository;

import com.kavinaya.minimessagequeue.entity.Message;
import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByQueueName(String queueName);

    List<Message> findByStatus(MessageStatus status);

    List<Message> findByQueueNameAndStatus(String queueName,
                                           MessageStatus status);

    Optional<Message> findFirstByQueueNameAndStatusOrderByCreatedAtAsc(
            String queueName,
            MessageStatus status
    );

}