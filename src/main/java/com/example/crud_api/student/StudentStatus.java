package com.example.crud_api.student;


public enum StudentStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    GRADUATED("Graduated"),
    WITHDRAWN("Withdrawn");
    private final String displayName;

    StudentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}