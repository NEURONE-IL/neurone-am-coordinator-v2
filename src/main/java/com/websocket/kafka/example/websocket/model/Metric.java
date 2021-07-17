package com.websocket.kafka.example.websocket.model;

public class Metric {
    
    private String username;
    private Float value;
    private String type;
    private String client;
    /**
     * @return the client
     */
    public String getClient() {
        return client;
    }


    /**
     * @param client the client to set
     */
    public void setClient(String client) {
        this.client = client;
    }



    /**
     * @return the type
     */
    public String getType() {
        return type;
    }


    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }


    /**
     * @return the value
     */
    public Float getValue() {
        return value;
    }
    
}
