package org.example.dziennikmonolith.controller;

import org.example.dziennikmonolith.model.User;
import org.example.dziennikmonolith.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // Do budowania URI dla odpowiedzi CREATED

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users); // Status 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user)) // Jeśli znaleziono -> 200 OK
                .orElse(ResponseEntity.notFound().build()); // Jeśli nie znaleziono -> 404 Not Found
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Można dodać walidację @Valid dla obiektu user
        User createdUser = userService.createUser(user);

        // Dobrą praktyką jest zwrócenie URI nowo utworzonego zasobu w nagłówku Location
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdUser); // Status 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        // Można dodać walidację @Valid
        return userService.updateUser(id, userDetails)
                .map(updatedUser -> ResponseEntity.ok(updatedUser)) // Jeśli zaktualizowano -> 200 OK
                .orElse(ResponseEntity.notFound().build()); // Jeśli nie znaleziono -> 404 Not Found
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // Jeśli usunięto -> 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Jeśli nie znaleziono -> 404 Not Found
        }
    }
}
