package com.websocket.kafka.example.websocket.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fanout.gripcontrol.GripControl;
import org.fanout.gripcontrol.GripPubControl;
import org.fanout.gripcontrol.WebSocketEvent;
import org.fanout.gripcontrol.WebSocketMessageFormat;
import org.fanout.pubcontrol.Format;
import org.fanout.pubcontrol.Item;
import org.fanout.pubcontrol.PublishFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.websocket.kafka.example.websocket.clients.Client;
import com.websocket.kafka.example.websocket.model.ClientData;
import com.websocket.kafka.example.websocket.model.Metric;
import com.websocket.kafka.example.websocket.util.Utils;

@Controller
public class ClientController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private Client clientData;

    @Autowired
    private GripPubControl pub;

    @PostMapping(value = "/api/clients")
    public ResponseEntity<?> createClient(@RequestBody ClientData client) {
        try {

            if (client.getName() == "") {
                return Utils.exceptionCatch(null, "The field name is required", "Field name not found", 400);
            }

            String name = client.getName();

            Set<String> metrics;
            Set<String> users;
            if (client.getMetrics() != null) {
                metrics = client.getMetrics();
            } else {
                metrics = new HashSet<>();
            }

            if (client.getUsers() != null) {
                users = client.getUsers();
            } else {
                users = new HashSet<>();
            }

            String err = clientData.addClient(name, metrics, users);

            if (err != "") {
                System.out.println(err);
                return Utils.exceptionCatch(null, err, "", 500);
            }

            // HttpHeaders responseHeaders = new HttpHeaders();
            // responseHeaders.set("Content-Type","text/plain");
            // responseHeaders.set("Grip-Hold", "stream");
            // responseHeaders.set("Grip-Channel",name);
            return ResponseEntity.ok("{\"status\": \"creado\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.exceptionCatch(e, "Error to create client", e.getMessage(), 500);
        }

    }

    @GetMapping(value = "/sse")
    public ResponseEntity<?> sse(@RequestParam(required = true) List<String> clients) {

        try {
            String channels = String.join(",", clients);
            System.out.println("channels");
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "text/event-stream");
            responseHeaders.set("Grip-Hold", "stream");
            responseHeaders.set("Grip-Channel", channels);
            // Utils.publishValue();
            return ResponseEntity.ok().headers(responseHeaders).body("Stream init");
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.exceptionCatch(e, "Error to create client", e.getMessage(), 500);
        }
    }

    @PostMapping(value = "/ws")
    public ResponseEntity<?> websocket(@RequestBody String body) {

        try {
            List<WebSocketEvent> inEvents = GripControl.decodeWebSocketEvents(body);

            if (inEvents != null && inEvents.size() > 0) {

                Map<String, Object> channel = new HashMap<String, Object>();
                if (inEvents.get(0).type.equals("OPEN")) {
                    // channel.put("channel", client);

                    List<WebSocketEvent> outEvents = new ArrayList<WebSocketEvent>();
                    outEvents.add(new WebSocketEvent("OPEN"));
                    // outEvents.add(new WebSocketEvent("TEXT",
                    // "c:" + GripControl.webSocketControlMessage("subscribe", channel)));

                    String responseBody = GripControl.encodeWebSocketEvents(outEvents);
                    System.out.println("respuesta " + responseBody);
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set("Content-Type", "application/websocket-events");
                    responseHeaders.set("Sec-WebSocket-Extensions", "grip; message-prefix=\"\"");
                    return ResponseEntity.ok().headers(responseHeaders).body(responseBody);
                }
                if (inEvents.get(0).type.equals("TEXT")) {
                    String json = inEvents.get(0).content;
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = mapper.readValue(json, Map.class);

                    String action = map.get("action");
                    String channelName = map.get("client");
                    channel.put("channel", channelName);

                    if (!action.equals("subscribe") && !action.equals("unsubscribe")) {
                        return Utils.exceptionCatch(null, "Acción no permitida", "Action not allowed", 500);
                    }

                    List<WebSocketEvent> outEvents = new ArrayList<WebSocketEvent>();
                    // outEvents.add(new WebSocketEvent("OPEN"));
                    outEvents.add(
                            new WebSocketEvent("TEXT", "c:" + GripControl.webSocketControlMessage(action, channel)));

                    String responseBody = GripControl.encodeWebSocketEvents(outEvents);
                    System.out.println("respuesta " + responseBody);
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.set("Content-Type", "application/websocket-events");
                    responseHeaders.set("Sec-WebSocket-Extensions", "grip; message-prefix=\"\"");
                    return ResponseEntity.ok().headers(responseHeaders).body(responseBody);

                }

                return ResponseEntity.ok("ok");

            } else {
                return ResponseEntity.ok("No open");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Utils.exceptionCatch(e, "Error to create client", e.getMessage(), 500);

        }
    }

    @PostMapping(value = "/api/publish/{name}")
    public ResponseEntity<?> publish(@RequestBody Metric metric, @PathVariable(name = "name") String name) {
        System.out.println("Aqui me caigo");
        System.out.println(metric.getUserId());
        List<String> channels = new ArrayList<String>();
        channels.add(name);
        try {

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(metric);
            List<Format> formats = Arrays.asList((Format) new WebSocketMessageFormat(json));
            // System.out.println("Aqui me caigo");
            // System.out.println("Aqui me caigo 2");
            System.out.print(json);
            json = json.replaceAll("\n", "");
            String m = String.format("event: %s\ndata: %s\n\n", name, json);
            pub.publishHttpStream(channels, m);
            pub.publish(channels, new Item(formats, null, null));
            return ResponseEntity.ok("publicado");
        } catch (PublishFailedException | JsonProcessingException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return Utils.exceptionCatch(e, "Error to create client", e.getMessage(), 500);

        }
    }

    @PutMapping(value = "/api/clients/{name}")
    public ResponseEntity<?> updateClient(@RequestBody ClientData client, @PathVariable("name") String name) {

        try {
            if (name == "") {
                return Utils.exceptionCatch(null, "The field name is required", "Field name not found", 400);
            }

            Set<String> metrics;
            Set<String> users;
            if (client.getMetrics() != null) {
                metrics = client.getMetrics();
            } else {
                metrics = new HashSet<>();
            }

            if (client.getUsers() != null) {
                users = client.getUsers();
            } else {
                users = new HashSet<>();
            }

            String err = clientData.updateMetricsOrUsers(name, metrics, users);

            if (err != "") {
                return Utils.exceptionCatch(null, err, "", 500);
            }
            return ResponseEntity.ok("Actualizado");
        } catch (Exception e) {
            return Utils.exceptionCatch(e, "Error to create client", "", 500);
        }
    }

    @DeleteMapping(value = "/api/clients/{name}")
    @ResponseBody
    public ResponseEntity<?> deleteClient(@PathVariable("name") String name) {

        try {
            if (name == "") {
                return Utils.exceptionCatch(null, "The field name is required", "Field name not found", 400);
            }

            String err = clientData.deleteClient(name);

            if (err != "") {
                return Utils.exceptionCatch(null, err, "", 500);
            }
            return ResponseEntity.ok("Eliminado");
        } catch (Exception e) {
            return Utils.exceptionCatch(e, "Error to create client", "", 500);
        }
    }

    @DeleteMapping(value = "/api/clients/{name}/elements")
    @ResponseBody
    public ResponseEntity<?> deleteClientUsersOrMetrics(@RequestBody ClientData client,
            @PathVariable("name") String name) {

        try {

            if (name == "") {
                return Utils.exceptionCatch(null, "The field name is required", "Field name not found", 400);
            }

            Set<String> metrics;
            Set<String> users;
            if (client.getMetrics() != null) {
                metrics = client.getMetrics();
            } else {
                metrics = new HashSet<>();
            }

            if (client.getUsers() != null) {
                users = client.getUsers();
            } else {
                users = new HashSet<>();
            }

            String err = clientData.deleteClientElements(name, metrics, users);

            if (err != "") {
                return Utils.exceptionCatch(null, err, "", 500);
            }
            return ResponseEntity.ok("Elementos eliminados");

        } catch (Exception e) {
            return Utils.exceptionCatch(e, "Error to create client", "", 500);
        }
    }

    @GetMapping(value = "/api/clients")
    public ResponseEntity<?> getAllClients() {

        List<HashMap<String, Object>> clients = new ArrayList<>();

        for (String client : clientData.getAllClients()) {

            HashMap<String, Object> clientObject = new HashMap<>();
            clientObject.put("name", client);
            clientObject.put("metrics", clientData.getMetricsByclient(client));
            clientObject.put("users", clientData.getUsersByClient(client));

            clients.add(clientObject);
        }

        return ResponseEntity.ok(clients);

    }

    // @PostMapping(value = "/api/topic/{topic}")
    // @ResponseBody
    // public void createListener(@PathVariable("topic") String topic) {

    // try {
    // if (topic == null || topic.length() < 4) {
    // System.out.println("Bad request");
    // return;
    // }
    // KafkaConsumerUtil.StartListener(topic, template);
    // System.out.println("Topic created");
    // } catch (Exception e) {

    // throw new RuntimeException(e);
    // }

    // }
}
