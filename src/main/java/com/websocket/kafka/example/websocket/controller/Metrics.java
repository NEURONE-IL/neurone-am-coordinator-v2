package com.websocket.kafka.example.websocket.controller;

import java.util.Arrays;

import com.websocket.kafka.example.websocket.model.MetricInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
@RestController
public class Metrics {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ArrayList<MetricInfo> metricsList;

    private final String baseUrl="http://localhost:7070";


    @RequestMapping(value= "/api/users/{username}/metrics/{metric}")
    public String getMetricValueByuser(@PathVariable(value = "username") String username,
    @PathVariable(value= "metric") String metric){

        HttpHeaders headers= new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity <String> entity= new HttpEntity<String>(headers);

        String url= baseUrl+"/keyvalue/"+metric+"-store/"+username;
        return restTemplate.exchange(url,HttpMethod.GET,entity,String.class).getBody();

    }

    @GetMapping(value = "/api/metrics")
    public ArrayList<MetricInfo> getMetricList(){

        try {
            System.out.println("aaaaaaaaaaaa "+metricsList);
            return metricsList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
            //TODO: handle exception
        }

    }
    
}
