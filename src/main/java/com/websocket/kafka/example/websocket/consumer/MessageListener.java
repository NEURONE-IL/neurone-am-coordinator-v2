package com.websocket.kafka.example.websocket.consumer;

import com.websocket.kafka.example.websocket.clients.Client;
import com.websocket.kafka.example.websocket.constants.KafkaConstants;

import com.websocket.kafka.example.websocket.model.Metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MessageListener {
    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    Client clientData;

    @KafkaListener(topics={"totalcover","bmrelevant","precision"}, groupId = KafkaConstants.GROUP_ID)
    public void listen(Metric metric){

       
        String userId= metric.getUsername();

        if (clientData.checkUser(userId)){

            Set<String> clients= clientData.getUserClient(userId);

            String metricName=metric.getType();

            for(String client: clients){

                if(clientData.checkClientMetric(client, metricName)){
                    System.out.println("Sending "+metric.getType()+" for "+metric.getUsername()+" with value "+metric.getValue()+ "to channel "+client);
                    template.convertAndSend("/topic/"+client,metric);
                }
            }

        }
    }



}
