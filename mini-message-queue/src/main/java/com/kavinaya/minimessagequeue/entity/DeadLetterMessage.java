package com.kavinaya.minimessagequeue.entity;

import com.kavinaya.minimessagequeue.entity.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dead_letter_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeadLetterMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long originalMessageId;

    @Column(nullable = false)
    private String queueName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus originalStatus;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime failedAt;
}