package com.websocket.kafka.example.websocket.kafka;

/** @author "Bikas Katwal" 20/02/19 */
public interface IMessageProcessor<T> {

    void process(String topic,String key, T json);
  }