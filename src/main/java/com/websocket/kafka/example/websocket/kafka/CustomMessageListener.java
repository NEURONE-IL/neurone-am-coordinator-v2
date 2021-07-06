package com.websocket.kafka.example.websocket.kafka;



import lombok.AllArgsConstructor;

import com.websocket.kafka.example.websocket.model.Metric;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

/** @author "Bikas Katwal" 13/03/19 */
@AllArgsConstructor
public class CustomMessageListener implements MessageListener<String, Metric> {

  // inject your own concrete processor
  private IMessageProcessor<Metric> messageProcessor;

  @Override
  public void onMessage(ConsumerRecord<String, Metric> consumerRecord) {
    // process message
    messageProcessor.process(consumerRecord.topic(),consumerRecord.key(), consumerRecord.value());
  }
}