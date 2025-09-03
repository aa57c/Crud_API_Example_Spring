package com.example.crud_api.student;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Only use IDENTITY if your DB auto-generates IDs
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "passport_number", nullable = false)
    private String passportNumber;

    public Student() {}

    public Student(String name, String passportNumber) {
        this.name = name;
        this.passportNumber = passportNumber;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
}
