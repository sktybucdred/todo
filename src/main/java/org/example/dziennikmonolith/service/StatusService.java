package org.example.dziennikmonolith.service;

import jakarta.transaction.Transactional;
import org.example.dziennikmonolith.model.Status;
import org.example.dziennikmonolith.model.User;
import org.example.dziennikmonolith.repositories.StatusRepository;
import org.example.dziennikmonolith.repositories.TaskRepository;
import org.example.dziennikmonolith.repositories.UserRepository; // <-- dorzucone
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {
    private final StatusRepository statusRepo;
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;    // <-- dorzucone

    public StatusService(StatusRepository statusRepo,
                         TaskRepository taskRepo,
                         UserRepository userRepo) {  // <-- dorzucone
        this.statusRepo = statusRepo;
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    private User currentUser() {
        // pobieramy nazwę zalogowanego użytkownika
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        // i ładujemy encję z bazy
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika: " + username));
    }

    public List<Status> listForCurrentUser() {
        return statusRepo.findByUser(currentUser());
    }
    public Status findById(Long id) {
        return statusRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono statusu o podanym ID: " + id));
    }
    @Transactional
    public Status create(Status status) {
        User user = currentUser();
        status.setUser(user);
        if (statusRepo.existsByUserAndName(user, status.getName())) {
            throw new RuntimeException("Status o tej nazwie już istnieje");
        }
        return statusRepo.save(status);
    }

    @Transactional
    public Status update(Long id, Status form) {
        User user = currentUser();
        Status existing = statusRepo.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Nie znaleziono statusu"));
        if (!existing.getName().equals(form.getName()) &&
                statusRepo.existsByUserAndName(user, form.getName())) {
            throw new RuntimeException("Status o tej nazwie już istnieje");
        }
        existing.setName(form.getName());
        return statusRepo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        User user = currentUser();
        Status status = statusRepo.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Nie znaleziono statusu"));
        boolean inUse = taskRepo.existsByUserAndStatus(user, status);
        if (inUse) {
            throw new RuntimeException("Nie można usunąć statusu powiązanego z zadaniami");
        }
        statusRepo.delete(status);
    }
}
