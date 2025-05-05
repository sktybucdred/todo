package org.example.dziennikmonolith.model.enums;

import jakarta.persistence.Table;

@Table(name = "status")
public enum Status {
    NEW,
    IN_PROGRESS,
    COMPLETED
}
