package org.example.dziennikmonolith.service;

import jakarta.transaction.Transactional;
import org.example.dziennikmonolith.model.*;
import org.example.dziennikmonolith.model.Task;
import org.example.dziennikmonolith.repositories.TaskRepository;
import org.example.dziennikmonolith.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final CategoryService categoryService;
    private final StatusService statusService;

    public TaskService(TaskRepository taskRepo, UserRepository userRepo, CategoryService categoryService, StatusService statusService) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.categoryService = categoryService;
        this.statusService = statusService;
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

    public List<Task> listForCurrentUser() {
        return taskRepo.findByUser(currentUser());
    }
    @Transactional
    public Task create(Task task, Long categoryId, Long statusId) {
        User user = currentUser();
        task.setUser(user);

        Category category = categoryService.findById(categoryId);

        Status status = statusService.findById(statusId);
        task.setCategory(category);
        task.setStatus(status);
        if (taskRepo.existsByUserAndTitle(user, task.getTitle())) {
            throw new RuntimeException("Task o tej nazwie już istnieje");
        }
        return taskRepo.save(task);
    }

    @Transactional
    public Task update(Long id, Task form, Long categoryId, Long statusId) {
        User user = currentUser();
        Task existing = taskRepo.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Nie znaleziono statusu"));
        if (!existing.getTitle().equals(form.getTitle()) &&
                taskRepo.existsByUserAndTitle(user, form.getTitle())) {
            throw new RuntimeException("Task o tej nazwie już istnieje");
        }
        existing.setTitle(form.getTitle());
        existing.setDescription(form.getDescription());
        Status newStatus = statusService.findById(statusId);
        existing.setStatus(newStatus);
        Category newCategory = categoryService.findById(categoryId);
        existing.setCategory(newCategory);
        return taskRepo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        User user = currentUser();
        Task task = taskRepo.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Nie znaleziono statusu"));
        taskRepo.delete(task);
    }

    public Optional<Task> findById(Long id) {
        return taskRepo.findById(id)
                .filter(s -> s.getUser().equals(currentUser()));
    }
}
