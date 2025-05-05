package org.example.dziennikmonolith.controller;

import jakarta.validation.Valid;
import org.example.dziennikmonolith.model.Category;
import org.example.dziennikmonolith.model.Status;
import org.example.dziennikmonolith.model.Task;
import org.example.dziennikmonolith.service.CategoryService;
import org.example.dziennikmonolith.service.StatusService;
import org.example.dziennikmonolith.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final StatusService statusService;

    @Autowired
    public TaskController(TaskService taskService, CategoryService categoryService, StatusService statusService) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.statusService = statusService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", taskService.listForCurrentUser());
        model.addAttribute("categories", categoryService.listForCurrentUser());
        model.addAttribute("statuses", statusService.listForCurrentUser());

        return "tasks/list";
    }

    @GetMapping("/new")
    public String showCreate(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryService.listForCurrentUser());
        model.addAttribute("statuses", statusService.listForCurrentUser());

        return "tasks/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("task") @Valid Task task,
                         @RequestParam(name = "categoryId", required = false) Long categoryId,
                         @RequestParam(name = "statusId", required = false) Long statusId,                         BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryService.listForCurrentUser());
            model.addAttribute("statuses", statusService.listForCurrentUser());
            return "tasks/form";
        }
        try {
            taskService.create(task, categoryId, statusId);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "tasks/form";
        }
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        Optional<Task> taskOptional = taskService.findById(id);

        // Sprawdź, czy task istnieje i czy użytkownik ma do niego dostęp
        if (!taskOptional.isPresent()) {
            // Jeśli task nie istnieje lub brak dostępu, rzuć wyjątek lub zwróć stronę błędu
            throw new RuntimeException("Task o podanym ID nie istnieje lub brak uprawnień do edycji."); // Można też zwrócić np. "error/404"
        }

        Task taskToEdit = taskOptional.get();

        model.addAttribute("task", taskToEdit); // Dodaj taska do modelu
        model.addAttribute("categories", categoryService.listForCurrentUser()); // Dodaj dostępne kategorie
        model.addAttribute("statuses", statusService.listForCurrentUser());   // Dodaj dostępne statusy

        return "tasks/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute("task") @Valid Task task,
                       BindingResult br,
                       @RequestParam(name = "categoryId", required = false) Long categoryId,
                       @RequestParam(name = "statusId", required = false) Long statusId,Model model) {
        if (br.hasErrors()) return "task/form";
        try {
            taskService.update(id, task, categoryId, statusId);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("categories", categoryService.listForCurrentUser());
            model.addAttribute("statuses", statusService.listForCurrentUser());
            model.addAttribute("task", task);
            return "tasks/form";
        }
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        try {
            taskService.delete(id);
        } catch (RuntimeException ex) {
            model.addAttribute("deleteError", ex.getMessage());
            return list(model);
        }
        return "redirect:/tasks";
    }
}
