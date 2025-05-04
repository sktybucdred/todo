package org.example.dziennikmonolith.controller;

import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.model.User; // Import User
import org.example.dziennikmonolith.repositories.UserRepository; // Import UserRepository
import org.example.dziennikmonolith.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Do pobrania danych użytkownika
import org.springframework.security.core.userdetails.UserDetails; // Standardowy interfejs Spring Security
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    private final CategoryService categoryService;
    private final UserRepository userRepository; // Potrzebne do pobrania pełnego obiektu User

    @Autowired
    public CategoryViewController(CategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    // --- Metoda pomocnicza do pobierania aktualnego użytkownika ---
    private Optional<User> getCurrentUser(UserDetails userDetails) {
        if (userDetails == null) {
            return Optional.empty(); // Nie zalogowany
        }
        // Znajdź użytkownika w bazie na podstawie nazwy z UserDetails
        return userRepository.findByUsername(userDetails.getUsername());
    }

    /**
     * Wyświetla listę kategorii TYLKO dla zalogowanego użytkownika.
     */
    @GetMapping
    public String showCategoryList(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Optional<User> currentUserOpt = getCurrentUser(userDetails);
        if (currentUserOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Musisz być zalogowany, aby zobaczyć swoje kategorie.");
            return "redirect:/login"; // Przekieruj na logowanie
        }

        List<Category> categories = categoryService.getAllCategoriesByUser(currentUserOpt.get());
        model.addAttribute("categories", categories);
        return "categories/list"; // Oczekuje: templates/categories/list.html
    }


    /**
     * Wyświetla formularz do tworzenia nowej kategorii.
     */
    @GetMapping("/new")
    public String showCreateCategoryForm(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (getCurrentUser(userDetails).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Musisz być zalogowany.");
            return "redirect:/login";
        }
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Dodaj nową kategorię");
        return "categories/form"; // Oczekuje: templates/categories/form.html
    }

    /**
     * Obsługuje tworzenie nowej kategorii dla zalogowanego użytkownika.
     */
    @PostMapping("/save")
    public String handleCreateCategoryForm(@ModelAttribute Category category,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {

        Optional<User> currentUserOpt = getCurrentUser(userDetails);
        if (currentUserOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sesja wygasła lub nie jesteś zalogowany.");
            return "redirect:/login";
        }

        try {
            Category savedCategory = categoryService.createCategoryForUser(category, currentUserOpt.get());
            redirectAttributes.addFlashAttribute("successMessage", "Kategoria '" + savedCategory.getName() + "' została dodana.");
        } catch (CategoryService.CategoryNameExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Można by wrócić do formularza zamiast listy, przekazując dane z powrotem
            redirectAttributes.addFlashAttribute("category", category); // Przekaż obiekt z powrotem do formularza
            return "redirect:/categories/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił nieoczekiwany błąd: " + e.getMessage());
        }
        return "redirect:/categories";
    }

    /**
     * Wyświetla formularz edycji kategorii, jeśli należy do zalogowanego użytkownika.
     */
    @GetMapping("/{id}/edit")
    public String showEditCategoryForm(@PathVariable Long id, Model model,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {

        Optional<User> currentUserOpt = getCurrentUser(userDetails);
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Category> categoryOpt = categoryService.getCategoryByIdAndUser(id, currentUserOpt.get());

        if (categoryOpt.isPresent()) {
            model.addAttribute("category", categoryOpt.get());
            model.addAttribute("pageTitle", "Edytuj kategorię (ID: " + id + ")");
            return "categories/form"; // Ten sam formularz co przy tworzeniu
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono kategorii o ID: " + id + " lub nie masz do niej dostępu.");
            return "redirect:/categories";
        }
    }

    /**
     * Obsługuje aktualizację kategorii należącej do zalogowanego użytkownika.
     */
    @PostMapping("/{id}/update")
    public String handleUpdateCategoryForm(@PathVariable Long id, @ModelAttribute Category categoryDetails,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes, Model model) { // Dodano Model

        Optional<User> currentUserOpt = getCurrentUser(userDetails);
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        // Ustawiamy ID w obiekcie, bo nie ma go w @ModelAttribute
        categoryDetails.setId(id);

        try {
            Optional<Category> updatedCategoryOpt = categoryService.updateCategoryForUser(id, categoryDetails, currentUserOpt.get());

            if (updatedCategoryOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("successMessage", "Kategoria '" + updatedCategoryOpt.get().getName() + "' została zaktualizowana.");
                return "redirect:/categories";
            } else {
                // Ten case nie powinien wystąpić jeśli updateCategoryForUser rzuca wyjątek gdy nie znajdzie
                redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono kategorii o ID: " + id + " do aktualizacji lub nie masz do niej dostępu.");
                return "redirect:/categories";
            }
        } catch (CategoryService.CategoryNameExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Wróć do formularza edycji, przekazując błędne dane
            model.addAttribute("pageTitle", "Edytuj kategorię (ID: " + id + ")");
            model.addAttribute("category", categoryDetails); // Prześlij dane z formularza z powrotem
            return "categories/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił nieoczekiwany błąd podczas aktualizacji: " + e.getMessage());
            // Można też wrócić do formularza
            model.addAttribute("pageTitle", "Edytuj kategorię (ID: " + id + ")");
            model.addAttribute("category", categoryDetails);
            return "categories/form";
        }
    }


    /**
     * Obsługuje usunięcie kategorii należącej do zalogowanego użytkownika.
     */
    @PostMapping("/{id}/delete")
    public String handleDeleteCategory(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {

        Optional<User> currentUserOpt = getCurrentUser(userDetails);
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        try {
            boolean deleted = categoryService.deleteCategoryForUser(id, currentUserOpt.get());
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Kategoria o ID " + id + " została usunięta.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono kategorii o ID: " + id + " lub nie masz uprawnień do jej usunięcia.");
            }
        } catch (Exception e) { // Np. błąd więzów integralności
            redirectAttributes.addFlashAttribute("errorMessage", "Nie można usunąć kategorii (ID: " + id + "). Może być powiązana z innymi danymi. Błąd: " + e.getMessage());
        }
        return "redirect:/categories";
    }
}
