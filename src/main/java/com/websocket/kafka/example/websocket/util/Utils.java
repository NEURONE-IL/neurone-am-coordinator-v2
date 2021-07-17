package com.websocket.kafka.example.websocket.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fanout.gripcontrol.GripPubControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;



public class Utils {

    @Autowired
    private static GripPubControl pub;


    public static ResponseEntity<?> exceptionCatch (java.lang.Exception e, String friendlyMessague , String realMessague , Integer status){
        Map<String,Object> response = new HashMap<String,Object>();
        response.put("clientMessage", friendlyMessague);
        response.put("message", realMessague);
        response.put("error", e.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @Async
    public static void publishValue(){

        
        try {
          
            System.out.println("publicandoo");
            List<String> channels = new ArrayList<String>();
            System.out.println("Estableciendo conexión");
            channels.add("session");
        
            pub.publishHttpStream(channels, "event:session\ndata:init\n\n");
            
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
}
