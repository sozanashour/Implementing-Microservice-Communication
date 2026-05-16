package com.example.user_service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public UserController() {
        save(new User("1", "Sozan", "sozan@example.com"));
        save(new User("2", "Ahmad", "ahmad@example.com"));
        save(new User("3", "Sara", "sara@example.com"));
        idCounter.set(4);
    }

    private void save(User user) {
        users.put(user.getId(), user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = users.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        String id = String.valueOf(idCounter.getAndIncrement());
        user.setId(id);
        users.put(id, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
