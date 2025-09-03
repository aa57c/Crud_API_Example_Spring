package com.example.crud_api.student;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Student Controller", description = "APIs for Student operations")
public class StudentResource {

    private final StudentService studentService;

    public StudentResource(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    @Operation(summary = "Get all students", description = "Returns a list of students")
    public ResponseEntity<List<Student>> retrieveAllStudents() {
        List<Student> students = studentService.findAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{id}")
    @Operation(summary = "Get a student based on id", description = "Returns the student with matching id")
    public ResponseEntity<Student> retrieveStudent(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        Student student = studentService.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        return ResponseEntity.ok(student);
    }

    @PostMapping("/students")
    @Operation(summary = "Add a student")
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.createStudent(student);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStudent.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedStudent);
    }

    @PutMapping("/students/{id}")
    @Operation(summary = "update a student's info")
    public ResponseEntity<Student> updateStudent(@Valid @RequestBody Student student,
                                                 @PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        Student updatedStudent = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/students/{id}")
    @Operation(summary = "Remove a student")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
