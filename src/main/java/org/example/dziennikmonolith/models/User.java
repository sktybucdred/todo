package org.example.dziennikmonolith.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    /* Implementacja logowania z wykorzystaniem Spring Security.
Powiązanie danych (kategorii i zadań) z zalogowanym użytkownikiem.
Własny formularz logowania.
Możliwość wylogowania użytkownika.
Opcjonalnie rejestracja nowych użytkowników.
*/

}
