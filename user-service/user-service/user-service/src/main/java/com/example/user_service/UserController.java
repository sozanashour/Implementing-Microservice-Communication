package com.example.user_service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public Map<String, String> getUser(@PathVariable String id) {
        Map<String, String> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "Sozan");
        return user;
    }
}