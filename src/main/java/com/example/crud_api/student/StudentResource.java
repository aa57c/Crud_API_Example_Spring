package com.example.crud_api.student;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StudentResource {

    private final StudentService studentService;

    public StudentResource(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> retrieveAllStudents() {
        List<Student> students = studentService.findAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> retrieveStudent(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        Student student = studentService.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        return ResponseEntity.ok(student);
    }

    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.createStudent(student);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStudent.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedStudent);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@Valid @RequestBody Student student,
                                                 @PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        Student updatedStudent = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
