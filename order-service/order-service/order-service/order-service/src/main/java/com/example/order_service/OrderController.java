package com.example.order_service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Value("${user.service.url}")
    private String userServiceBaseUrl;

    private final RestTemplate restTemplate;
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public OrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        save(new Order("1", "1", "Laptop",  1, 1500.00));
        save(new Order("2", "2", "Phone",   2,  800.00));
        save(new Order("3", "1", "Headset", 1,  150.00));
        idCounter.set(4);
    }

    private void save(Order order) {
        orders.put(order.getOrderId(), order);
    }

    @GetMapping
    public List<Map<String, Object>> getAllOrders() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Order order : orders.values()) {
            result.add(toResponse(order));
        }
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable String id) {
        Order order = orders.get(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(order));
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            restTemplate.getForObject(userServiceBaseUrl + "/users/" + order.getUserId(), Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found with id: " + order.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        String id = String.valueOf(idCounter.getAndIncrement());
        order.setOrderId(id);
        order.setStatus("PENDING");
        orders.put(id, order);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }

    private Map<String, Object> toResponse(Order order) {
        Map<String, Object> response = new HashMap<>();
        response.put("orderId",     order.getOrderId());
        response.put("productName", order.getProductName());
        response.put("quantity",    order.getQuantity());
        response.put("totalPrice",  order.getTotalPrice());
        response.put("status",      order.getStatus());

        try {
            Map<String, String> user = restTemplate.exchange(
                userServiceBaseUrl + "/users/" + order.getUserId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, String>>() {}
            ).getBody();
            response.put("user", user);
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            Map<String, String> fallback = new HashMap<>();
            fallback.put("id",    order.getUserId());
            fallback.put("error", "user-service unavailable");
            response.put("user", fallback);
        }

        return response;
    }
}
