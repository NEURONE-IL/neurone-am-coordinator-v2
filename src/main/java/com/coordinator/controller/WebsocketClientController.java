package com.coordinator.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.coordinator.clients.Client;
import com.coordinator.model.ClientData;
import com.coordinator.util.Utils;

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
