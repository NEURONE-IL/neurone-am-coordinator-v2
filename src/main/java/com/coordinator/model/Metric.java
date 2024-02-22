package com.coordinator.model;

import java.util.Map;

public class Metric {

    private String userId;
    private Float value;
    private String type;
    private String client;
    private Map<String, Object> metadata;

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
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the value
     */
    public Float getValue() {
        return value;
    }

    public Object getMetadata() {
        return metadata;
    }

}
