package com.websocket.kafka.example.websocket.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public class Utils {
    public static ResponseEntity<?> exceptionCatch (java.lang.Exception e, String friendlyMessague , String realMessague , Integer status){
        Map<String,Object> response = new HashMap<String,Object>();
        response.put("clientMessage", friendlyMessague);
        response.put("message", realMessague);
        response.put("error", e.getMessage());
        return ResponseEntity.status(status).body(response);
    }
}
