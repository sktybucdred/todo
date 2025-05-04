package org.example.dziennikmonolith.controller;

import jakarta.validation.Valid;
import org.example.dziennikmonolith.model.User;
import org.example.dziennikmonolith.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("userForm") @Valid User userForm,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.registerNew(userForm);
        } catch (RuntimeException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "auth/register";
        }
        return "redirect:/login?registered";
    }
}
