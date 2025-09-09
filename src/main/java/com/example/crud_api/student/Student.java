package com.example.crud_api.student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Student entity representing a student record in the system")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the student", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Full name of the student", example = "John Doe", minLength = 2, maxLength = 100)
    private String name;

    @Column(name = "passport_number", nullable = false, unique = true)
    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "^[A-Z][0-9]{7}$", message = "Passport number must be in format: one letter followed by 7 digits (e.g., A1234567)")
    @Schema(description = "Passport number - one uppercase letter followed by 7 digits", example = "A1234567", pattern = "^[A-Z][0-9]{7}$")
    private String passportNumber;

    @Column(nullable = false)
    @NotNull(message = "Age is required")
    @Min(value = 16, message = "Student must be at least 16 years old")
    @Max(value = 100, message = "Age cannot exceed 100")
    @Schema(description = "Age of the student", example = "25", minimum = "16", maximum = "100")
    private Integer age;

    @Column(unique = true)
    @Email(message = "Please provide a valid email address")
    @Schema(description = "Email address of the student", example = "john.doe@example.com")
    private String email;

    @Column(name = "enrollment_date")
    @NotNull(message = "Enrollment date is required")
    @Schema(description = "Date and time when the student enrolled", example = "2023-09-01T09:00:00")
    private LocalDateTime enrollmentDate;

    @Column(name = "graduation_year")
    @Min(value = 2020, message = "Graduation year must be 2020 or later")
    @Max(value = 2030, message = "Graduation year cannot exceed 2030")
    @Schema(description = "Expected graduation year", example = "2025", minimum = "2020", maximum = "2030")
    private Integer graduationYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status is required")
    @Schema(description = "Current status of the student", example = "ACTIVE", allowableValues = {"ACTIVE", "SUSPENDED", "GRADUATED", "WITHDRAWN"})
    private StudentStatus status = StudentStatus.ACTIVE;

    // Audit fields - automatically managed by JPA
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Timestamp when the record was created", example = "2023-09-01T09:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Timestamp when the record was last updated", example = "2023-09-01T09:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Version
    @Schema(description = "Version number for optimistic locking", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Long version; // For optimistic locking

    // Constructors
    public Student() {}

    public Student(String name, String passportNumber, Integer age, String email,
                   LocalDateTime enrollmentDate, Integer graduationYear) {
        this.name = name;
        this.passportNumber = passportNumber;
        this.age = age;
        this.email = email;
        this.enrollmentDate = enrollmentDate;
        this.graduationYear = graduationYear;
        this.status = StudentStatus.ACTIVE;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public StudentStatus getStatus() { return status; }
    public void setStatus(StudentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    // Business logic methods
    @Schema(hidden = true)
    public boolean isActive() {
        return status == StudentStatus.ACTIVE;
    }

    @Schema(hidden = true)
    public void activate() {
        this.status = StudentStatus.ACTIVE;
    }

    @Schema(hidden = true)
    public void suspend() {
        this.status = StudentStatus.SUSPENDED;
    }

    @Schema(hidden = true)
    public void graduate() {
        this.status = StudentStatus.GRADUATED;
    }
}