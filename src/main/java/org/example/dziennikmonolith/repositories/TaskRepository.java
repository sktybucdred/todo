package org.example.dziennikmonolith.repositories;

import org.example.dziennikmonolith.model.Status;
import org.example.dziennikmonolith.model.Task;
import org.example.dziennikmonolith.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Custom query methods can be defined here if needed
    // For example, find tasks by status or user
    List<Task> findByStatus(Status status);
    List<Task> findByUser(User user);
    boolean existsByUserAndStatus(User user, Status status);

}
