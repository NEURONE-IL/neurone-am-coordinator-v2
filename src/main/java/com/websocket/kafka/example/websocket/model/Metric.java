package com.websocket.kafka.example.websocket.model;

public class Metric {

    private String username;
    private Float value;
    private String type;

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
