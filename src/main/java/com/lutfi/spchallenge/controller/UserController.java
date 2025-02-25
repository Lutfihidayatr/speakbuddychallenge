package com.lutfi.spchallenge.controller;

import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.exception.UserNotFoundException;
import com.lutfi.spchallenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> findUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUser(id);
        return userOpt.map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // TODO: don't do this in production, always require offset and limit
    //  for retrieve more than 1 data
    @GetMapping("/users")
    public ResponseEntity<List<User>> test() {
        return ResponseEntity.ok(userService.getUsers());
    }
}
