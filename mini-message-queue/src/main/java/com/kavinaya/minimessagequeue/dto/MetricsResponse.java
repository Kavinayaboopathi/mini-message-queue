package com.kavinaya.minimessagequeue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetricsResponse {

    private long pending;
    private long processing;
    private long success;
    private long dead;
}