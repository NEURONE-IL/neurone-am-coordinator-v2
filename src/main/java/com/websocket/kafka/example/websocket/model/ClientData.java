package com.websocket.kafka.example.websocket.model;

import java.util.Set;

public class ClientData {
    
    private String name;
    private Set<String> metrics;
    private Set<String> users;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the metrics
     */
    public Set<String> getMetrics() {
        return metrics;
    }
    /**
     * @param metrics the metrics to set
     */
    public void setMetrics(Set<String> metrics) {
        this.metrics = metrics;
    }
    /**
     * @return the users
     */
    public Set<String> getUsers() {
        return users;
    }
    /**
     * @param users the users to set
     */
    public void setUsers(Set<String> users) {
        this.users = users;
    }

}
