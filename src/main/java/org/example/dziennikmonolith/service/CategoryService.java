package org.example.dziennikmonolith.service;

import jakarta.transaction.Transactional;
import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.model.User; // Importuj User
import org.example.dziennikmonolith.repositories.CategoryRepository;
import org.example.dziennikmonolith.repositories.StatusRepository;
import org.example.dziennikmonolith.repositories.TaskRepository;
import org.example.dziennikmonolith.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;    // <-- dorzucone

    public CategoryService(CategoryRepository categoryRepo, TaskRepository taskRepo, UserRepository userRepo) {
        this.categoryRepo = categoryRepo;
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

    public List<Category> listForCurrentUser() {
        return categoryRepo.findByUser(currentUser());
    }

    @Transactional
    public Category create(Category Category) {
        User user = currentUser();
        Category.setUser(user);
        if (categoryRepo.existsByUserAndName(user, Category.getName())) {
            throw new RuntimeException("Category o tej nazwie już istnieje");
        }
        return categoryRepo.save(Category);
    }

    @Transactional
    public Category update(Long id, Category form) {
        User user = currentUser();
        Category existing = categoryRepo.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Nie znaleziono statusu"));
        if (!existing.getName().equals(form.getName()) &&
                categoryRepo.existsByUserAndName(user, form.getName())) {
            throw new RuntimeException("Category o tej nazwie już istnieje");
        }
        existing.setName(form.getName());
        return categoryRepo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        User user = currentUser();
        Category Category = categoryRepo.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Nie znaleziono statusu"));
        boolean inUse = taskRepo.existsByUserAndCategory(user, Category);
        if (inUse) {
            throw new RuntimeException("Nie można usunąć kategorii powiązanej z zadaniami");
        }
        categoryRepo.delete(Category);
    }

    public Category findById(Long categoryId) {
        Optional<Category> category = categoryRepo.findById(categoryId);
        if (category.isPresent()) {
            return category.get();
        } else {
            throw new RuntimeException("Nie znaleziono kategorii o podanym ID: " + categoryId);
        }
    }
}
