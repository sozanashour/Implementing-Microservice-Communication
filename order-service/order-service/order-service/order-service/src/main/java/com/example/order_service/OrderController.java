package com.example.order_service;


import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    @GetMapping("/orders/{id}")
    public Map<String, Object> getOrder(@PathVariable String id) {
        RestTemplate restTemplate = new RestTemplate();
        String userUrl = "http://localhost:8080/users/1"; 
        Map<String, String> user = restTemplate.getForObject(userUrl, Map.class);

        Map<String, Object> order = new HashMap<>();
        order.put("orderId", id);
        order.put("user", user); 
        return order;
    }
}