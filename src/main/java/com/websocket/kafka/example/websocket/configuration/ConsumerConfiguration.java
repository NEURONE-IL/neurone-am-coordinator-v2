package com.websocket.kafka.example.websocket.configuration;

import com.websocket.kafka.example.websocket.constants.KafkaConstants;
import com.websocket.kafka.example.websocket.model.Message;
import com.websocket.kafka.example.websocket.model.Metric;
import com.websocket.kafka.example.websocket.model.Metric;
import com.websocket.kafka.example.websocket.model.VisitedLink;
import com.websocket.kafka.example.websocket.model.Metric;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class ConsumerConfiguration {
    
    @Bean
    ConcurrentKafkaListenerContainerFactory<String,Metric> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String,Metric> factory= new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
    
        return factory;
    }

    @Bean
    public ConsumerFactory<String,Metric> consumerFactory(){

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations(), new StringDeserializer(), new JsonDeserializer<>(Metric.class));
    }

    
    @Bean
    public Map<String,Object> consumerConfigurations(){

        Map<String, Object> configurations = new HashMap<>();
        configurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKER);
        configurations.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID);
        configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configurations.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return configurations;
    }

        

}
