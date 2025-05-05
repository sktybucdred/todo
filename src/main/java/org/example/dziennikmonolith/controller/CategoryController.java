package org.example.dziennikmonolith.controller;

import jakarta.validation.Valid;
import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.model.User; // Import User
import org.example.dziennikmonolith.repositories.UserRepository; // Import UserRepository
import org.example.dziennikmonolith.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Do pobrania danych użytkownika
import org.springframework.security.core.userdetails.UserDetails; // Standardowy interfejs Spring Security
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.listForCurrentUser());
        return "category/list";
    }

    @GetMapping("/new")
    public String showCreate(Model model) {
        model.addAttribute("category", new Category());
        return "category/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("category") @Valid Category category,
                         BindingResult br, Model model) {
        if (br.hasErrors()) return "category/form";
        try {
            categoryService.create(category);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "category/form";
        }
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService
                .listForCurrentUser().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Category nie istnieje")));
        return "category/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute("category") @Valid Category category,
                       BindingResult br, Model model) {
        if (br.hasErrors()) return "category/form";
        try {
            categoryService.update(id, category);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "category/form";
        }
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        try {
            categoryService.delete(id);
        } catch (RuntimeException ex) {
            model.addAttribute("deleteError", ex.getMessage());
            return list(model);
        }
        return "redirect:/categories";
    }
}
