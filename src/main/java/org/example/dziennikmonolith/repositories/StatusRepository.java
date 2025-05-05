package org.example.dziennikmonolith.repositories;

import org.example.dziennikmonolith.model.Status;
import org.example.dziennikmonolith.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {
    List<Status> findByUser(User user);
    boolean existsByUserAndName(User user, String name);
}
