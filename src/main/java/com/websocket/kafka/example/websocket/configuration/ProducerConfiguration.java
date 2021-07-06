// package com.websocket.kafka.example.websocket.configuration;

// import org.apache.kafka.clients.producer.ProducerConfig;
// import org.apache.kafka.common.serialization.StringSerializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.annotation.EnableKafka;
// import org.springframework.kafka.core.DefaultKafkaProducerFactory;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.core.ProducerFactory;
// import org.springframework.kafka.support.serializer.JsonSerializer;

// import java.util.HashMap;
// import java.util.Map;

// import com.websocket.kafka.example.websocket.constants.KafkaConstants;
// import com.websocket.kafka.example.websocket.model.Message;

// @EnableKafka
// @Configuration
// public class ProducerConfiguration {
//     @Bean
//     public ProducerFactory<String, Message> producerFactory() {
//         return new DefaultKafkaProducerFactory<>(producerConfigurations());
//     }

//     @Bean
//     public Map<String, Object> producerConfigurations() {
//         Map<String, Object> configurations = new HashMap<>();
//         configurations.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKER);
//         configurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//         configurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//         return configurations;
//     }

//     @Bean
//     public KafkaTemplate<String, Message> kafkaTemplate() {
//         return new KafkaTemplate<>(producerFactory());
//     }
// }