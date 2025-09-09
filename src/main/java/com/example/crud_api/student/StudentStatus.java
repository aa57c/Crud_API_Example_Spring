package com.example.crud_api.student;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration of possible student statuses")
public enum StudentStatus {
    @Schema(description = "Student is actively enrolled and attending")
    ACTIVE("Active"),

    @Schema(description = "Student is temporarily suspended from studies")
    SUSPENDED("Suspended"),

    @Schema(description = "Student has completed their studies and graduated")
    GRADUATED("Graduated"),

    @Schema(description = "Student has withdrawn from the institution")
    WITHDRAWN("Withdrawn");

    private final String displayName;

    StudentStatus(String displayName) {
        this.displayName = displayName;
    }

    @Schema(description = "Human-readable display name for the status")
    public String getDisplayName() {
        return displayName;
    }
}