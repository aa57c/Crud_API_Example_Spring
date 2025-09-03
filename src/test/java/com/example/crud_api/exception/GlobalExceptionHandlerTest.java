package com.example.crud_api.exception;

import com.example.crud_api.student.StudentNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/students/123");
    }

    @Test
    void handleStudentNotFoundException_ShouldReturn404() {
        // Given
        StudentNotFoundException exception = new StudentNotFoundException("Student not found with id: 123");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleStudentNotFoundException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(404);
        assertThat(errorResponse.getError()).isEqualTo("Not Found");
        assertThat(errorResponse.getMessage()).isEqualTo("Student not found with id: 123");
        assertThat(errorResponse.getPath()).isEqualTo("/api/v1/students/123");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    void handleValidationException_ShouldReturn400WithDetails() {
        // Given
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "student");
        bindingResult.addError(new FieldError("student", "name", "Name is required"));
        bindingResult.addError(new FieldError("student", "age", "Age must be at least 16"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleValidationException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getError()).isEqualTo("Validation Failed");
        assertThat(errorResponse.getMessage()).isEqualTo("Invalid input data");
        assertThat(errorResponse.getPath()).isEqualTo("/api/v1/students/123");
        assertThat(errorResponse.getTimestamp()).isNotNull();

        List<String> details = errorResponse.getDetails();
        assertThat(details).hasSize(2);
        assertThat(details).contains("name: Name is required");
        assertThat(details).contains("age: Age must be at least 16");
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Student ID must be positive");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleIllegalArgumentException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getError()).isEqualTo("Bad Request");
        assertThat(errorResponse.getMessage()).isEqualTo("Student ID must be positive");
        assertThat(errorResponse.getPath()).isEqualTo("/api/v1/students/123");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        // Given
        RuntimeException exception = new RuntimeException("Unexpected error occurred");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleGenericException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(500);
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(errorResponse.getPath()).isEqualTo("/api/v1/students/123");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    void errorResponse_Constructor_ShouldSetAllFields() {
        // When
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", "Test message", "/test/path");

        // Then
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getError()).isEqualTo("Bad Request");
        assertThat(errorResponse.getMessage()).isEqualTo("Test message");
        assertThat(errorResponse.getPath()).isEqualTo("/test/path");
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }

    @Test
    void errorResponse_DefaultConstructor_ShouldSetTimestamp() {
        // When
        ErrorResponse errorResponse = new ErrorResponse();

        // Then
        assertThat(errorResponse.getTimestamp()).isNotNull();
    }
}
