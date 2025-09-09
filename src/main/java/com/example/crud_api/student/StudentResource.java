package com.example.crud_api.student;

import com.example.crud_api.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Student Management", description = "APIs for managing student records including CRUD operations and status management")
public class StudentResource {

    private final StudentService studentService;

    public StudentResource(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    @Operation(
            summary = "Retrieve all students",
            description = "Fetches a list of all students in the system with their complete information including audit details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all students",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Student.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<Student>> retrieveAllStudents() {
        List<Student> students = studentService.findAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{id}")
    @Operation(
            summary = "Retrieve a specific student",
            description = "Fetches detailed information about a student by their unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Student found and retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Student.class),
                            examples = @ExampleObject(
                                    name = "Student Example",
                                    value = """
                                    {
                                        "id": 1,
                                        "name": "John Doe",
                                        "passportNumber": "A1234567",
                                        "age": 25,
                                        "email": "john.doe@example.com",
                                        "enrollmentDate": "2023-09-01T09:00:00",
                                        "graduationYear": 2025,
                                        "status": "ACTIVE",
                                        "createdAt": "2023-09-01T09:00:00",
                                        "updatedAt": "2023-09-01T09:00:00",
                                        "version": 0
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid student ID provided (must be positive number)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Student> retrieveStudent(
            @Parameter(description = "Unique identifier of the student", example = "1", required = true)
            @PathVariable Long id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        Student student = studentService.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        return ResponseEntity.ok(student);
    }

    @PostMapping("/students")
    @Operation(
            summary = "Create a new student",
            description = "Creates a new student record with validation. The enrollment date will be set to current time if not provided, and status will be set to ACTIVE."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Student created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Student.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data - validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Validation Error Example",
                                    value = """
                                    {
                                        "timestamp": "2023-09-01T10:30:00",
                                        "status": 400,
                                        "error": "Validation Failed",
                                        "message": "Invalid input data",
                                        "path": "/api/v1/students",
                                        "details": [
                                            "name: Name is required",
                                            "passportNumber: Passport number must be in format: one letter followed by 7 digits",
                                            "age: Student must be at least 16 years old"
                                        ]
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error (e.g., duplicate passport number or email)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Student> createStudent(
            @Parameter(description = "Student data to create", required = true)
            @Valid @RequestBody Student student) {

        Student savedStudent = studentService.createStudent(student);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStudent.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedStudent);
    }

    @PutMapping("/students/{id}")
    @Operation(
            summary = "Update an existing student",
            description = "Updates student information. Note: passport number and enrollment date cannot be modified for security reasons."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Student updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Student.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid student ID or validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Student> updateStudent(
            @Parameter(description = "Updated student data", required = true)
            @Valid @RequestBody Student student,
            @Parameter(description = "Unique identifier of the student to update", example = "1", required = true)
            @PathVariable Long id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        Student updatedStudent = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/students/{id}")
    @Operation(
            summary = "Delete a student",
            description = "Permanently removes a student record from the system. This operation cannot be undone."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Student deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid student ID provided (must be positive number)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Unique identifier of the student to delete", example = "1", required = true)
            @PathVariable Long id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }

        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // Additional endpoints for status management
    @PutMapping("/students/{id}/suspend")
    @Operation(
            summary = "Suspend a student",
            description = "Changes the student status to SUSPENDED"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Student suspended successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Student.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Student> suspendStudent(
            @Parameter(description = "Student ID", example = "1", required = true)
            @PathVariable Long id) {

        Student suspendedStudent = studentService.suspendStudent(id);
        return ResponseEntity.ok(suspendedStudent);
    }

    @PutMapping("/students/{id}/activate")
    @Operation(
            summary = "Activate a student",
            description = "Changes the student status to ACTIVE"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Student activated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Student.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Student> activateStudent(
            @Parameter(description = "Student ID", example = "1", required = true)
            @PathVariable Long id) {

        Student activatedStudent = studentService.activateStudent(id);
        return ResponseEntity.ok(activatedStudent);
    }

    @PutMapping("/students/{id}/graduate")
    @Operation(
            summary = "Graduate a student",
            description = "Changes the student status to GRADUATED"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Student graduated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Student.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Student> graduateStudent(
            @Parameter(description = "Student ID", example = "1", required = true)
            @PathVariable Long id) {

        Student graduatedStudent = studentService.graduateStudent(id);
        return ResponseEntity.ok(graduatedStudent);
    }

    @GetMapping("/students/active")
    @Operation(
            summary = "Retrieve all active students",
            description = "Fetches a list of students with ACTIVE status only"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved active students",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Student.class))
            )
    )
    public ResponseEntity<List<Student>> retrieveActiveStudents() {
        List<Student> activeStudents = studentService.findActiveStudents();
        return ResponseEntity.ok(activeStudents);
    }
}