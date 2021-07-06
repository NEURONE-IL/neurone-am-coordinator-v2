package com.websocket.kafka.example.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
public class VisitedLink {
    private String username;
    private String url;

    @JsonProperty("payload")
    private void unpackNested(Map<String,Object> payload) {
        this.username = (String)payload.get("username");
        this.url=(String)payload.get("url");
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }
    
  
}
