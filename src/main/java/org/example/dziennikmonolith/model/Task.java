package org.example.dziennikmonolith.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description")
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
