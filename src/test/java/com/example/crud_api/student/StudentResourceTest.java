package com.example.crud_api.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentResource.class)
class StudentResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    private ObjectMapper objectMapper;
    private Student testStudent;
    private List<Student> studentList;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testStudent = new Student(
                "John Doe",
                "A1234567",
                25,
                "john@example.com",
                LocalDateTime.now(),
                2025
        );
        testStudent.setId(1L);

        Student student2 = new Student(
                "Jane Smith",
                "B7654321",
                22,
                "jane@example.com",
                LocalDateTime.now().minusMonths(6),
                2026
        );
        student2.setId(2L);

        studentList = Arrays.asList(testStudent, student2);
    }

    @Test
    void retrieveAllStudents_ShouldReturnStudentList() throws Exception {
        // Given
        when(studentService.findAllStudents()).thenReturn(studentList);

        // When & Then
        mockMvc.perform(get("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].passportNumber", is("A1234567")))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].passportNumber", is("B7654321")));

        verify(studentService).findAllStudents();
    }

    @Test
    void retrieveStudent_WithValidId_ShouldReturnStudent() throws Exception {
        // Given
        when(studentService.findStudentById(1L)).thenReturn(Optional.of(testStudent));

        // When & Then
        mockMvc.perform(get("/api/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.passportNumber", is("A1234567")))
                .andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(studentService).findStudentById(1L);
    }

    @Test
    void retrieveStudent_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        when(studentService.findStudentById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/students/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Student not found with id: 999")));

        verify(studentService).findStudentById(999L);
    }

    @Test
    void retrieveStudent_WithNegativeId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/students/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Student ID must be a positive number")));

        verify(studentService, never()).findStudentById(any());
    }

    @Test
    void createStudent_WithValidData_ShouldReturn201() throws Exception {
        // Given
        Student newStudent = new Student(
                "Alice Brown",
                "C9876543",
                20,
                "alice@example.com",
                LocalDateTime.now(),
                2027
        );

        Student savedStudent = new Student(
                "Alice Brown",
                "C9876543",
                20,
                "alice@example.com",
                LocalDateTime.now(),
                2027
        );
        savedStudent.setId(3L);

        when(studentService.createStudent(any(Student.class))).thenReturn(savedStudent);

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/students/3")))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Alice Brown")))
                .andExpect(jsonPath("$.passportNumber", is("C9876543")));

        verify(studentService).createStudent(any(Student.class));
    }

    @Test
    void createStudent_WithInvalidData_ShouldReturn400() throws Exception {
        // Given - Student with invalid data (missing name, invalid passport format)
        Student invalidStudent = new Student(
                "", // Empty name
                "INVALID", // Invalid passport format
                15, // Age too young
                "invalid-email", // Invalid email
                LocalDateTime.now(),
                2019 // Invalid graduation year
        );

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStudent)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))));

        verify(studentService, never()).createStudent(any());
    }

    @Test
    void updateStudent_WithValidData_ShouldReturn200() throws Exception {
        // Given
        Student updateData = new Student(
                "John Updated",
                "A1234567",
                26,
                "john.updated@example.com",
                LocalDateTime.now(),
                2024
        );

        Student updatedStudent = new Student(
                "John Updated",
                "A1234567",
                26,
                "john.updated@example.com",
                LocalDateTime.now(),
                2024
        );
        updatedStudent.setId(1L);

        when(studentService.updateStudent(eq(1L), any(Student.class))).thenReturn(updatedStudent);

        // When & Then
        mockMvc.perform(put("/api/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.age", is(26)))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")));

        verify(studentService).updateStudent(eq(1L), any(Student.class));
    }

    @Test
    void updateStudent_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        Student updateData = new Student(
                "John Updated",
                "A1234567",
                26,
                "john.updated@example.com",
                LocalDateTime.now(),
                2024
        );

        when(studentService.updateStudent(eq(999L), any(Student.class)))
                .thenThrow(new StudentNotFoundException("Student not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/v1/students/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Student not found with id: 999")));

        verify(studentService).updateStudent(eq(999L), any(Student.class));
    }

    @Test
    void deleteStudent_WithValidId_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(studentService).deleteStudent(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(studentService).deleteStudent(1L);
    }

    @Test
    void deleteStudent_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        doThrow(new StudentNotFoundException("Student not found with id: 999"))
                .when(studentService).deleteStudent(999L);

        // When & Then
        mockMvc.perform(delete("/api/v1/students/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Student not found with id: 999")));

        verify(studentService).deleteStudent(999L);
    }

    @Test
    void deleteStudent_WithNegativeId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/students/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Student ID must be a positive number")));

        verify(studentService, never()).deleteStudent(any());
    }
}