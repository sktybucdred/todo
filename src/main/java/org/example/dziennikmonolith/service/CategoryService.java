package org.example.dziennikmonolith.service;

import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.model.User; // Importuj User
import org.example.dziennikmonolith.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Ważne dla metod modyfikujących

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Pobiera wszystkie kategorie dla danego użytkownika.
     */
    public List<Category> getAllCategoriesByUser(User user) {
        return categoryRepository.findByUser(user);
    }

    /**
     * Pobiera kategorię po ID, upewniając się, że należy do danego użytkownika.
     */
    public Optional<Category> getCategoryByIdAndUser(Long id, User user) {
        return categoryRepository.findByIdAndUser(id, user);
    }

    /**
     * Tworzy nową kategorię dla użytkownika, sprawdzając unikalność nazwy.
     * Rzuca wyjątek, jeśli nazwa już istnieje dla tego użytkownika.
     */
    @Transactional // Zapewnia spójność operacji
    public Category createCategoryForUser(Category category, User user) throws CategoryNameExistsException {
        // Sprawdź, czy kategoria o tej nazwie już istnieje dla tego użytkownika
        if (categoryRepository.existsByNameAndUser(category.getName(), user)) {
            throw new CategoryNameExistsException("Kategoria o nazwie '" + category.getName() + "' już istnieje dla tego użytkownika.");
        }
        // Ustaw właściciela kategorii
        category.setUser(user);
        // Zapisz kategorię
        return categoryRepository.save(category);
    }

    /**
     * Aktualizuje istniejącą kategorię użytkownika.
     * Sprawdza, czy użytkownik jest właścicielem.
     * Sprawdza, czy nowa nazwa nie koliduje z inną kategorią tego użytkownika.
     */
    @Transactional
    public Optional<Category> updateCategoryForUser(Long id, Category categoryDetails, User user) throws CategoryNameExistsException {
        // Znajdź istniejącą kategorię należącą do użytkownika
        return categoryRepository.findByIdAndUser(id, user)
                .map(existingCategory -> {
                    // Sprawdź, czy nowa nazwa nie jest już zajęta przez INNĄ kategorię tego użytkownika
                    if (!existingCategory.getName().equals(categoryDetails.getName()) &&
                            categoryRepository.existsByNameAndUserAndIdNot(categoryDetails.getName(), user, id)) {
                        throw new CategoryNameExistsException("Inna kategoria o nazwie '" + categoryDetails.getName() + "' już istnieje dla tego użytkownika.");
                    }
                    // Zaktualizuj nazwę
                    existingCategory.setName(categoryDetails.getName());
                    // Zapisz zmiany (nie trzeba ustawiać użytkownika, bo obiekt już go ma)
                    return categoryRepository.save(existingCategory);
                });
    }

    /**
     * Usuwa kategorię, upewniając się, że należy do danego użytkownika.
     * Zwraca true jeśli usunięto, false jeśli nie znaleziono.
     */
    @Transactional
    public boolean deleteCategoryForUser(Long id, User user) {
        // Znajdź kategorię należącą do użytkownika
        Optional<Category> categoryOpt = categoryRepository.findByIdAndUser(id, user);
        if (categoryOpt.isPresent()) {
            categoryRepository.delete(categoryOpt.get()); // Usuń znalezioną kategorię
            return true;
        }
        return false; // Kategoria nie znaleziona lub nie należy do użytkownika
    }

    // Prosta klasa wyjątku do obsługi konfliktu nazw
    public static class CategoryNameExistsException extends RuntimeException {
        public CategoryNameExistsException(String message) {
            super(message);
        }
    }
}
