package com.websocket.kafka.example.websocket.model;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetricInfo {
    
    private String name;
    private String alias;
    private HashMap<String,String> descriptions;
    private String dataType;
    private int max;
    private Double interval;

    public MetricInfo(){}
}
