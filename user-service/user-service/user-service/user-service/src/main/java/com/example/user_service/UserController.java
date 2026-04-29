package com.example.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public Map<String, String> getUser(@PathVariable String id) {
        Map<String, String> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "Sozan"); // بيانات وهمية
        return user;
    }
}