package com.kavinaya.minimessagequeue.repository;

import com.kavinaya.minimessagequeue.entity.Message;
import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByQueueName(String queueName);

    List<Message> findByStatus(MessageStatus status);

    List<Message> findByQueueNameAndStatus(String queueName,
                                           MessageStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Message> findFirstByQueueNameAndStatusOrderByCreatedAtAsc(
            String queueName,
            MessageStatus status
    );



    List<Message> findByStatusAndLockedAtBefore(
            MessageStatus status,
            LocalDateTime time
    );

    long countByStatus(MessageStatus status);

}