package com.kavinaya.minimessagequeue.dto;

import jakarta.validation.constraints.NotBlank;

public class PublishMessageRequest {

    @NotBlank(message = "Queue name cannot be empty")
    private String queueName;

    @NotBlank(message = "Message body cannot be empty")
    private String messageBody;

    public PublishMessageRequest() {
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}