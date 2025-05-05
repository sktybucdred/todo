package org.example.dziennikmonolith.controller;

import org.example.dziennikmonolith.service.CategoryService;
import org.example.dziennikmonolith.service.StatusService;
import org.example.dziennikmonolith.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final StatusService statusService;

    @Autowired
    public IndexController(TaskService taskService, CategoryService categoryService, StatusService statusService) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.statusService = statusService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", taskService.listForCurrentUser());
        model.addAttribute("categories", categoryService.listForCurrentUser());
        model.addAttribute("statuses", statusService.listForCurrentUser());
        return "/index";
    }

}
