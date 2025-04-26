package org.example.dziennikmonolith.service;

import org.example.dziennikmonolith.model.User;
import org.example.dziennikmonolith.repositories.TaskRepository;
import org.example.dziennikmonolith.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // Pobierz użytkownika po ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
        // W przyszłości można rzucać wyjątek jeśli nie znaleziono:
        // .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

     public Optional<User> getUserByUsername(String username) {
         return userRepository.findByUsername(username);
     }

    // Utwórz nowego użytkownika
    // Zgodnie z prośbą, hasło nie jest hashowane
    public User createUser(User user) {
        // Tutaj można dodać walidację, np. czy username/email już istnieje
        // if(userRepository.findByUsername(user.getUsername()).isPresent()) {
        //     throw new IllegalArgumentException("Username already exists");
        // }
        // Zapis hasła jako plain text
        return userRepository.save(user);
    }

    // Zaktualizuj użytkownika
    // Zwraca Optional, aby kontroler wiedział, czy aktualizacja się powiodła
    public Optional<User> updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(userDetails.getUsername());
                    existingUser.setName(userDetails.getName());
                    // Aktualizacja hasła (plain text)
                    existingUser.setPassword(userDetails.getPassword());
                    // Nie aktualizujemy ID!
                    return userRepository.save(existingUser);
                }); // Jeśli findById zwróci pusty Optional, map się nie wykona
    }

    // Usuń użytkownika
    // Zwraca boolean, czy operacja się powiodła
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
