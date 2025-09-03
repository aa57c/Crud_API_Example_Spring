package com.example.crud_api.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class StudentIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Clean database before each test
        studentRepository.deleteAll();
    }

    @Test
    void fullCrudWorkflow_ShouldWorkEndToEnd() throws Exception {
        // Step 1: Create a student
        Student newStudent = new Student(
                "Integration Test Student",
                "I1234567",
                23,
                "integration@example.com",
                LocalDateTime.now(),
                2025
        );

        String studentJson = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Integration Test Student")))
                .andExpect(jsonPath("$.passportNumber", is("I1234567")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Student createdStudent = objectMapper.readValue(studentJson, Student.class);
        Long studentId = createdStudent.getId();

        // Step 2: Retrieve the created student
        mockMvc.perform(get("/api/v1/students/" + studentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(studentId.intValue())))
                .andExpect(jsonPath("$.name", is("Integration Test Student")))
                .andExpect(jsonPath("$.passportNumber", is("I1234567")))
                .andExpect(jsonPath("$.email", is("integration@example.com")));

        // Step 3: Update the student
        Student updateData = new Student(
                "Updated Integration Student",
                "I1234567", // passport should not change
                24,
                "updated.integration@example.com",
                LocalDateTime.now(),
                2026
        );

        mockMvc.perform(put("/api/v1/students/" + studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(studentId.intValue())))
                .andExpect(jsonPath("$.name", is("Updated Integration Student")))
                .andExpect(jsonPath("$.age", is(24)))
                .andExpect(jsonPath("$.email", is("updated.integration@example.com")))
                .andExpect(jsonPath("$.graduationYear", is(2026)));

        // Step 4: Verify the update persisted
        mockMvc.perform(get("/api/v1/students/" + studentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Integration Student")))
                .andExpect(jsonPath("$.age", is(24)));

        // Step 5: Get all students (should contain our student)
        mockMvc.perform(get("/api/v1/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + studentId + ")].name", contains("Updated Integration Student")));

        // Step 6: Delete the student
        mockMvc.perform(delete("/api/v1/students/" + studentId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Step 7: Verify deletion
        mockMvc.perform(get("/api/v1/students/" + studentId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudent_WithDuplicatePassportNumber_ShouldFail() throws Exception {
        // Given - create first student
        Student firstStudent = new Student(
                "First Student",
                "D1234567",
                23,
                "first@example.com",
                LocalDateTime.now(),
                2025
        );

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstStudent)))
                .andExpect(status().isCreated());

        // When - try to create second student with same passport number
        Student duplicatePassportStudent = new Student(
                "Duplicate Passport",
                "D1234567", // Same passport number
                25,
                "duplicate@example.com",
                LocalDateTime.now(),
                2026
        );

        // Then - should fail with constraint violation
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatePassportStudent)))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // H2 constraint violation
    }

    @Test
    void createStudent_WithDuplicateEmail_ShouldFail() throws Exception {
        // Given - create first student
        Student firstStudent = new Student(
                "First Student",
                "E1234567",
                23,
                "duplicate@example.com",
                LocalDateTime.now(),
                2025
        );

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstStudent)))
                .andExpect(status().isCreated());

        // When - try to create second student with same email
        Student duplicateEmailStudent = new Student(
                "Duplicate Email",
                "F1234567",
                25,
                "duplicate@example.com", // Same email
                LocalDateTime.now(),
                2026
        );

        // Then - should fail with constraint violation
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailStudent)))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // H2 constraint violation
    }

    @Test
    void createStudent_WithInvalidValidationData_ShouldReturn400() throws Exception {
        // Given - student with multiple validation errors
        Student invalidStudent = new Student(
                "", // Empty name - should fail @NotBlank
                "INVALID", // Invalid passport format
                15, // Too young - should fail @Min(16)
                "not-an-email", // Invalid email format
                LocalDateTime.now(),
                2019 // Invalid graduation year - should fail @Min(2020)
        );

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStudent)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.details[*]", hasItem(containsString("Name is required"))))
                .andExpect(jsonPath("$.details[*]", hasItem(containsString("Passport number must be in format"))))
                .andExpect(jsonPath("$.details[*]", hasItem(containsString("Student must be at least 16 years old"))))
                .andExpect(jsonPath("$.details[*]", hasItem(containsString("Please provide a valid email address"))))
                .andExpect(jsonPath("$.details[*]", hasItem(containsString("Graduation year must be 2020 or later"))));
    }

    @Test
    void updateStudent_NonExistentStudent_ShouldReturn404() throws Exception {
        // Given
        Student updateData = new Student(
                "Non Existent",
                "N1234567",
                25,
                "nonexistent@example.com",
                LocalDateTime.now(),
                2025
        );

        // When & Then
        mockMvc.perform(put("/api/v1/students/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Student not found with id: 99999")));
    }

    @Test
    void deleteStudent_NonExistentStudent_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/students/99999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Student not found with id: 99999")));
    }

    @Test
    void getAllStudents_WithMultipleStudents_ShouldReturnAll() throws Exception {
        // Given - create multiple students
        Student student1 = new Student("Student One", "S1111111", 20, "one@example.com", LocalDateTime.now(), 2025);
        Student student2 = new Student("Student Two", "S2222222", 21, "two@example.com", LocalDateTime.now(), 2026);
        Student student3 = new Student("Student Three", "S3333333", 22, "three@example.com", LocalDateTime.now(), 2027);

        // Create students via API
        for (Student student : new Student[]{student1, student2, student3}) {
            mockMvc.perform(post("/api/v1/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(student)))
                    .andExpect(status().isCreated());
        }

        // When & Then
        mockMvc.perform(get("/api/v1/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Student One", "Student Two", "Student Three")))
                .andExpect(jsonPath("$[*].status", everyItem(is("ACTIVE"))));
    }
}