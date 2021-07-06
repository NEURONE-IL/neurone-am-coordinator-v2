package com.websocket.kafka.example.websocket.controller;

import com.websocket.kafka.example.websocket.model.ClientData;
import com.websocket.kafka.example.websocket.util.Utils;

import java.util.Set;

import com.websocket.kafka.example.websocket.clients.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketClientController {
    @Autowired
    private Client clientData;

    @MessageMapping("/client")
    public ResponseEntity<?> createClient(ClientData client){
        
        try {
            
            if(client.getName()==""){
                return Utils.exceptionCatch(null, "The field name is required", "Field name not found", 400);
            }
            String name=client.getName();

            Set<String> metrics=client.getMetrics();
            Set<String> users=client.getUsers();

            String err=clientData.addClient(name, metrics, users);

            if(err!=""){
                return Utils.exceptionCatch(null, err,"", 500);
            }
            return ResponseEntity.ok("Creado");
        } catch (Exception e) {
            return Utils.exceptionCatch(e, "Error to create client", "", 500);
            
        }

    }
}
