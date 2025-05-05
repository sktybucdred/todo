package org.example.dziennikmonolith.controller;

import jakarta.validation.Valid;
import org.example.dziennikmonolith.model.Status;
import org.example.dziennikmonolith.service.StatusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/statuses")
public class StatusController {
    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("statuses", statusService.listForCurrentUser());
        return "status/list";
    }

    @GetMapping("/new")
    public String showCreate(Model model) {
        model.addAttribute("status", new Status());
        return "status/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("status") @Valid Status status,
                         BindingResult br, Model model) {
        if (br.hasErrors()) return "status/form";
        try {
            statusService.create(status);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "status/form";
        }
        return "redirect:/statuses";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        model.addAttribute("status", statusService
                .listForCurrentUser().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Status nie istnieje")));
        return "status/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute("status") @Valid Status status,
                       BindingResult br, Model model) {
        if (br.hasErrors()) return "status/form";
        try {
            statusService.update(id, status);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "status/form";
        }
        return "redirect:/statuses";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        try {
            statusService.delete(id);
        } catch (RuntimeException ex) {
            model.addAttribute("deleteError", ex.getMessage());
            return list(model);
        }
        return "redirect:/statuses";
    }
}
