package org.example.dziennikmonolith.repositories;

import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.model.Status;
import org.example.dziennikmonolith.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
    boolean existsByUserAndName(User user, String name);}
