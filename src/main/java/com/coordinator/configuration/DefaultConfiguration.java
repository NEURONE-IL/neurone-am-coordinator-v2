package com.coordinator.configuration;

import com.coordinator.clients.Client;
import com.coordinator.model.MetricInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fanout.gripcontrol.*;
import org.fanout.pubcontrol.*;

@Configuration
public class DefaultConfiguration {
    
    @Bean
    public Client clientData(){
        Client client= new Client();
    
        return client;
          
    }

    @Bean
    public ArrayList<MetricInfo> metricsList(){
        ObjectMapper mapper = new ObjectMapper();

        TypeReference<List<MetricInfo>> typeReference = new TypeReference<List<MetricInfo>>(){};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/metrics.json");
        try {
            List<MetricInfo> metrics = mapper.readValue(inputStream,typeReference);
         
            return (ArrayList<MetricInfo>) metrics;

        } catch (IOException e){
            System.out.println("Unable to save users: " + e.getMessage());
            return new ArrayList<>();
        }

    }


    @Bean
    public GripPubControl pub(){
        List<Map<String, Object>> config = new ArrayList<Map<String, Object>>();
        Map<String, Object> entry = new HashMap<String, Object>();

        entry.put("control_uri", "http://localhost:5561");
        config.add(entry);
        GripPubControl pub = new GripPubControl(config);
        return pub;
    }
}
