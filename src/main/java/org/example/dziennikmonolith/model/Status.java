package org.example.dziennikmonolith.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.dziennikmonolith.model.User;

@Entity
@Data
@Table(
        name = "statuses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"})
)
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
