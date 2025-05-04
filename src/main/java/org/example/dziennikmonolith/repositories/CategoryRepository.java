package org.example.dziennikmonolith.repositories;

import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Znajdź wszystkie kategorie dla danego użytkownika
    List<Category> findByUser(User user);

    // Znajdź kategorię po ID ORAZ użytkowniku (upewnienie się, że użytkownik jest właścicielem)
    Optional<Category> findByIdAndUser(Long id, User user);

    // Sprawdź, czy kategoria o danej nazwie już istnieje dla tego użytkownika
    boolean existsByNameAndUser(String name, User user);

    // Opcjonalnie: Znajdź kategorię po nazwie i użytkowniku (jeśli potrzebne gdzieś indziej)
    Optional<Category> findByNameAndUser(String name, User user);

    // Dodatkowa metoda potrzebna przy aktualizacji: sprawdź czy istnieje inna kategoria
    // o tej samej nazwie dla danego użytkownika, wykluczając kategorię o podanym ID.
    boolean existsByNameAndUserAndIdNot(String name, User user, Long id);
}
