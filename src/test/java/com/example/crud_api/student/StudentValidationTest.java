package com.example.crud_api.student;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class StudentValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validStudent_ShouldPassValidation() {
        // Given
        Student student = new Student(
                "John Doe",
                "A1234567",
                25,
                "john@example.com",
                LocalDateTime.now(),
                2025
        );

        // When
        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void invalidName_ShouldFailValidation() {
        // Given - empty name
        Student student = new Student(
                "", // Invalid: empty name
                "A1234567",
                25,
                "john@example.com",
                LocalDateTime.now(),
                2025
        );

        // When
        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Student> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Name is required");
    }

    @Test
    void nameTooShort_ShouldFailValidation() {
        // Given - name too short
        Student student = new Student(
                "A", // Invalid: too short
                "A1234567",
                25,
                "john@example.com",
                LocalDateTime.now(),
                2025
        );

        // When
        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Student> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Name must be between 2 and 100 characters");
    }

    @Test
    void invalidPassportNumber_ShouldFailValidation() {
        // Given - invalid passport format
        Student student = new Student(
                "John Doe",
                "INVALID", // Invalid: doesn't match pattern
                25,
                "john@example.com",
                LocalDateTime.now(),
                2025
        );

        // When
        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Student> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("passportNumber");
        assertThat(violation.getMessage()).isEqualTo("Passport number must be in format: one letter followed by 7 digits (e.g., A1234567)");
    }

    @Test
    void ageTooLow_ShouldFailValidation() {
        // Given - age too low
        Student student = new Student(
                "John Doe",
                "A1234567",
                15, // Invalid: too young
                "john@example.com",
                LocalDateTime.now(),
                2025
        );

        // When
        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Student> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("age");
        assertThat(violation.getMessage()).isEqualTo("Student must be at least 16 years old");
    }

    @Test
    void ageTooHigh_ShouldFailValidation() {
        // Given - age too high
        Student student = new Student(
                "John Doe",
                "A1234567",
                101, // Invalid: too old
                "john@example.com",
                LocalDateTime.now(),
                2025
        );

        // When
        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Student> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("age");
        assertThat(violation.getMessage()).isEqualTo("Age cannot exceed 100");
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        // Given - invalid email format
        Student student = new Student(
                "John Doe",
                "A1234567",
                25,
                "not-an-email", // Invalid: not email format
                LocalDateTime.now(),
                2025
        );

        // When
        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Student> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("Please provide a valid email address");
    }
}