package com.kavinaya.minimessagequeue.mapper;

import com.kavinaya.minimessagequeue.dto.MessageResponse;
import com.kavinaya.minimessagequeue.entity.Message;

public class MessageMapper {

    private MessageMapper() {
    }

    public static MessageResponse toResponse(Message message) {

        MessageResponse response = new MessageResponse();

        response.setId(message.getId());
        response.setQueueName(message.getQueueName());
        response.setMessageBody(message.getMessageBody());
        response.setStatus(message.getStatus());
        response.setRetryCount(message.getRetryCount());
        response.setCreatedAt(message.getCreatedAt());

        return response;
    }
}