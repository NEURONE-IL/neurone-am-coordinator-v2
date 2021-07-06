package com.websocket.kafka.example.websocket.kafka;

import java.util.HashMap;
import java.util.Map;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;

import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import com.websocket.kafka.example.websocket.constants.KafkaConstants;
import com.websocket.kafka.example.websocket.model.Metric;
import com.websocket.kafka.example.websocket.processor.ConcreteMessageProcessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/** @author "Bikas Katwal" 13/03/19 */


public final class KafkaConsumerUtil {
    

  private static Map<String, ConcurrentMessageListenerContainer<String, Metric>> consumersMap =
  new HashMap<>();

  public static void StartListener(String topic,SimpMessagingTemplate template){

    Map<String, Object> configurations = new HashMap<>();
    configurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKER);
    configurations.put(ConsumerConfig.GROUP_ID_CONFIG, topic+"-id");
    configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    configurations.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    configurations.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);


    ConcreteMessageProcessor customProcessor = new ConcreteMessageProcessor(template);

    CustomMessageListener customMessageListener = new CustomMessageListener(customProcessor);
    System.out.println("Initializing topic "+ topic);
    KafkaConsumerUtil.startOrCreateConsumers(topic, customMessageListener, 1, configurations);
}
    /**
     * 
     * 
   * 1. This method first checks if consumers are already created for a given topic name 2. If the
   * consumers already exists in Map, it will just start the container and return 3. Else create a
   * new consumer and add to the Map
   *
   * @param topic topic name for which consumers is needed
   * @param messageListener pass implementation of MessageListener or AcknowledgingMessageListener
   *     based on enable.auto.commit
   * @param concurrency number of consumers you need
   * @param consumerProperties all the necessary consumer properties need to be passed in this
   */

  public static void startOrCreateConsumers(
      final String topic,
      final Object messageListener,
      final int concurrency,
      final Map<String, Object> consumerProperties) {
        


    ConcurrentMessageListenerContainer<String, Metric> container = consumersMap.get(topic);
    if (container != null) {
      if (!container.isRunning()) {
        System.out.println("Consumer already created for topic {}, starting consumer!!"+ topic);
        container.start();
        System.out.println("Consumer for topic {} started!!!!"+ topic);
      }
      return;
    }

    ContainerProperties containerProps = new ContainerProperties(topic);


    containerProps.setPollTimeout(100);
    Boolean enableAutoCommit = (Boolean) consumerProperties.get(ENABLE_AUTO_COMMIT_CONFIG);
    
    if (!enableAutoCommit) {

      containerProps.setAckMode(AckMode.MANUAL_IMMEDIATE);
    }
    
    ConsumerFactory<String, Metric> factory = new DefaultKafkaConsumerFactory<>(consumerProperties,new StringDeserializer(), new JsonDeserializer<>(Metric.class));

    container = new ConcurrentMessageListenerContainer<String,Metric>(factory, containerProps);
    
    if (enableAutoCommit && !(messageListener instanceof CustomMessageListener)) {
      throw new IllegalArgumentException(
          "Expected message listener of type com.bkatwal.kafka.impl.CustomMessageListener!");
    }

    // if (!enableAutoCommit && !(messageListener instanceof CustomAckMessageListener)) {
    //   throw new IllegalArgumentException(
    //       "Expected message listener of type com.bkatwal.kafka.impl.CustomAckMessageListener!");
    // }
    
    container.setupMessageListener(messageListener);

    if (concurrency == 0) {
      container.setConcurrency(1);
    } else {
      container.setConcurrency(concurrency);
    }
    

    container.start();
    // try {
        
    // } catch (Exception e) {
    //     Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            
    //         e.printStackTrace();
             
      
            
    //       }));
    //     //TODO: handle exception
    // }
    System.out.println("CONTAINER CREATED");
    consumersMap.put(topic, container);
    System.out.println("CONTAINER SAVED");

  }

  /**
   * Get the ListenerContainer from Map based on topic name and call stop on it, to stop all
   * consumers for given topic
   *
   * @param topic topic name to stop corresponding consumers
   */
  public static void stopConsumer(final String topic) {

    ConcurrentMessageListenerContainer<String, Metric> container = consumersMap.get(topic);
    container.stop();

  }

  private KafkaConsumerUtil() {
    throw new UnsupportedOperationException("Can not instantiate KafkaConsumerUtil");
  }
}
