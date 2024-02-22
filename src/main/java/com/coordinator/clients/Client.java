package com.coordinator.clients;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class Client {

    private static Map<String, Set<String>> usersMap = new ConcurrentHashMap<>();

    private static Map<String, Set<String>> clientsMap = new ConcurrentHashMap<>();

    public String addClient(String name, Set<String> metrics, Set<String> users) {

        if (name != "") {

            if (clientsMap.containsKey(name)) {
                return "The client exists";
            }
 
            clientsMap.put(name, new HashSet<>());
            Boolean flag = false;
            for (String metric : metrics) {
                if (metric != "") {
                    flag = true;
                    addMetric(name, metric);
                }
            }

            for (String user : users) {
                if (user != "") {
                    flag = true;
                    addClientToUser(name, user);
                }
            }
   

            if (flag) {
                return "";
            } else {
                return "The client was not created, metrics and users empty";
            }
        } else {
            return "name field is empty";
        }

    }

    public String updateMetricsOrUsers(String name, Set<String> metrics, Set<String> users) {
        if (!clientsMap.containsKey(name)) {
            return "Client not found";
        }
        for (String metric : metrics) {
            if (metric != "") {
                addMetric(name, metric);
            }
        }
        for (String user : users) {
            if (user != "") {
                addClientToUser(name, user);
            }
        }

        return "";

    }

    public void addClientToUser(String name, String user) {

        Set<String> userClients;
        if(usersMap.containsKey(user)){
            userClients = usersMap.get(user);

        } else{
            userClients= new HashSet<>();
            
        }
        userClients.add(name);
        usersMap.put(user, userClients);

    }

    public void addMetric(String name, String metric) {
        Set<String> metricsClient = clientsMap.get(name);
        metricsClient.add(metric);
        clientsMap.put(name, metricsClient);
    }



    public Boolean checkUser(String user) {

        return usersMap.containsKey(user);
    }

    public Boolean checkClientMetric(String name, String metric) {

        Set<String> clientMetrics = clientsMap.get(name);
        return clientMetrics.contains(metric);
        
    }

    public Set<String> getUserClient(String user) {
        return usersMap.get(user);
    }

    public void deleteUser(String user) {
        usersMap.remove(user);
    }

    public String deleteClient(String name){
        
        if(name==""){
            return "The client name is empty";
        }

        for(Map.Entry<String,Set<String>> entry: usersMap.entrySet() ){
            Set<String> userClients=entry.getValue();
            userClients.remove(name);
            usersMap.put(entry.getKey(), userClients);
        }

        clientsMap.remove(name);
        return "";
    }

    public String deleteClientElements(String name,Set<String> metrics, Set<String> users){
        
        if (!clientsMap.containsKey(name)) {
            return "Client not found";
        }

        for(String metric: metrics){
            Set<String> metricsClient = clientsMap.get(name);
            metricsClient.remove(metric);
            clientsMap.put(name, metricsClient);
        }

        for(String user: users){
            Set<String> userClients = usersMap.get(user);
            userClients.remove(name);
            usersMap.put(user,userClients);
        }

        return "";
    }


    public Set<String> getMetricsByclient(String name){
        return clientsMap.get(name);
    }

    public Set<String> getUsersByClient(String name){

        Set<String> clientUsers= new HashSet<>();
        
        for(Map.Entry<String,Set<String>> entry: usersMap.entrySet()){

            if(entry.getValue().contains(name)){
                clientUsers.add(entry.getKey());
            }
        }
        return clientUsers;
    }


    public Map<String, Set<String>> getUserMap() {
        return usersMap;
    }

    public Set<String> getAllClients(){

        return clientsMap.keySet();
    }
}
