package com.websocket.kafka.example.websocket.consumer;

import com.websocket.kafka.example.websocket.clients.Client;
import com.websocket.kafka.example.websocket.constants.KafkaConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.websocket.kafka.example.websocket.model.Metric;

import org.fanout.gripcontrol.GripPubControl;
import org.fanout.gripcontrol.WebSocketMessageFormat;
import org.fanout.pubcontrol.Format;
import org.fanout.pubcontrol.Item;
import org.fanout.pubcontrol.PublishFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class MessageListener {
    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    Client clientData;

    @Autowired
    private GripPubControl pub;

    @KafkaListener(topics = { "totalcover", "bmrelevant", "precision","writingtime","pagestay","totalpagestay","ifquotes","firstquerytime" }, groupId = KafkaConstants.GROUP_ID)
    public void listen(Metric metric) {

        System.out.println("metric:  type->"+metric.getType()+ ", user: ->"+metric.getUsername()+",value->"+metric.getValue());
        String userId = metric.getUsername();

        if (clientData.checkUser(userId)) {

            Set<String> clients = clientData.getUserClient(userId);

            String metricName = metric.getType();

            for (String client : clients) {

                if (clientData.checkClientMetric(client, metricName)) {
                    try {
                        List<String> channels = new ArrayList<String>();
                        channels.add(client);
                        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                        metric.setClient(client);
                        String json = ow.writeValueAsString(metric);

                        json = json.replaceAll("\n", "");
                        String m = String.format("event: %s\ndata: %s\n\n", client, json);
                        List<Format> formats = Arrays.asList((Format) new WebSocketMessageFormat(json));
                        System.out.println("Sending " + metric.getType() + " for " + metric.getUsername()
                                + " with value " + metric.getValue() + "to channel " + client);
                        pub.publishHttpStream(channels, m);
                        pub.publish(channels, new Item(formats, null, null));
                        // template.convertAndSend("/topic/"+client,metric);
                    } catch (PublishFailedException | JsonProcessingException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                }
            }

        }
    }

}
