package org.example.dziennikmonolith.models;

import jakarta.persistence.*;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private Status status; // NEW, IN_PROGRESS, COMPLETED

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
