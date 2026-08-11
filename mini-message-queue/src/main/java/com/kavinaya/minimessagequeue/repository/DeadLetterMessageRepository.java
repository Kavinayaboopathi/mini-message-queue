package com.kavinaya.minimessagequeue.repository;

import com.kavinaya.minimessagequeue.entity.DeadLetterMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadLetterMessageRepository
        extends JpaRepository<DeadLetterMessage, Long> {
}
