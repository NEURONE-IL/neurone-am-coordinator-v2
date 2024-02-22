package com.coordinator.consumer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.fanout.gripcontrol.GripPubControl;
import org.fanout.gripcontrol.WebSocketMessageFormat;
import org.fanout.pubcontrol.Format;
import org.fanout.pubcontrol.Item;
import org.fanout.pubcontrol.PublishFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.coordinator.clients.Client;
import com.coordinator.constants.KafkaConstants;
import com.coordinator.model.Metric;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
public class MessageListener {

    @Autowired
    Client clientData;

    @Autowired
    private GripPubControl pub;

    @KafkaListener(topics = { "totalcover", "bmrelevant", "precision", "writingtime", "pagestay", "totalpagestay",
            "ifquotes", "firstquerytime", "challengestarted" }, groupId = KafkaConstants.GROUP_ID)
    public void listen(Metric metric) {

        System.out.println("metric:  type->" + metric.getType() + ", user: ->" + metric.getUserId() + ",value->"
                + metric.getValue());
        String userId = metric.getUserId();

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
                        System.out.println("Sending " + metric.getType() + " for " + metric.getUserId()
                                + " with value " + metric.getValue() + "to channel " + client);
                        pub.publishHttpStream(channels, m);
                        pub.publish(channels, new Item(formats, null, null));
                    } catch (PublishFailedException | JsonProcessingException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                }
            }

        }
    }

}
