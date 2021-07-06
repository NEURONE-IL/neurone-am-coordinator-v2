package com.websocket.kafka.example.websocket.processor;

import com.websocket.kafka.example.websocket.kafka.IMessageProcessor;

import com.websocket.kafka.example.websocket.model.Metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConcreteMessageProcessor  implements IMessageProcessor<Metric>{
    

  
   private  SimpMessagingTemplate template;

    @Override
    public void process(String topic, String key, Metric value) {
      // logic to process message and save to target
      System.out.println("topic: "+topic+", key: "+key +", value: "+value.getValue());

      template.convertAndSend("/topic/"+topic,value);
    }
}
