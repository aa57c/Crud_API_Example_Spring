package com.example.crud_api.student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private Student existingStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student(
                "John Doe",
                "A1234567",
                25,
                "john@example.com",
                LocalDateTime.now(),
                2025
        );
        testStudent.setId(1L);

        existingStudent = new Student(
                "Jane Smith",
                "B7654321",
                22,
                "jane@example.com",
                LocalDateTime.now().minusMonths(6),
                2026
        );
        existingStudent.setId(2L);
    }

    @Test
    void findAllStudents_ShouldReturnAllStudents() {
        // Given
        List<Student> expectedStudents = Arrays.asList(testStudent, existingStudent);
        when(studentRepository.findAll()).thenReturn(expectedStudents);

        // When
        List<Student> actualStudents = studentService.findAllStudents();

        // Then
        assertThat(actualStudents).hasSize(2);
        assertThat(actualStudents).containsExactlyInAnyOrder(testStudent, existingStudent);
        verify(studentRepository).findAll();
    }

    @Test
    void findStudentById_WithValidId_ShouldReturnStudent() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // When
        Optional<Student> result = studentService.findStudentById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testStudent);
        verify(studentRepository).findById(1L);
    }

    @Test
    void findStudentById_WithInvalidId_ShouldReturnEmpty() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Student> result = studentService.findStudentById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(studentRepository).findById(999L);
    }

    @Test
    void createStudent_ShouldSaveAndReturnStudent() {
        // Given
        Student newStudent = new Student(
                "Alice Brown",
                "C9876543",
                20,
                "alice@example.com",
                null, // enrollment date will be set automatically
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
        savedStudent.setStatus(StudentStatus.ACTIVE);

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // When
        Student result = studentService.createStudent(newStudent);

        // Then
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Alice Brown");
        assertThat(result.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        assertThat(result.getEnrollmentDate()).isNotNull();

        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudent_WithoutEnrollmentDate_ShouldSetCurrentDate() {
        // Given
        Student newStudent = new Student(
                "Bob Wilson",
                "D1111111",
                23,
                "bob@example.com",
                null,
                2025
        );

        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student student = invocation.getArgument(0);
            student.setId(4L);
            return student;
        });

        // When
        Student result = studentService.createStudent(newStudent);

        // Then
        assertThat(result.getEnrollmentDate()).isNotNull();
        assertThat(result.getEnrollmentDate()).isBeforeOrEqualTo(LocalDateTime.now());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void updateStudent_WithValidId_ShouldUpdateAndReturnStudent() {
        // Given
        Student updateData = new Student(
                "John Updated",
                "A1234567", // passport shouldn't be updated
                26,
                "john.updated@example.com",
                LocalDateTime.now(),
                2024
        );

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Student result = studentService.updateStudent(1L, updateData);

        // Then
        assertThat(result.getName()).isEqualTo("John Updated");
        assertThat(result.getAge()).isEqualTo(26);
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(result.getGraduationYear()).isEqualTo(2024);
        assertThat(result.getPassportNumber()).isEqualTo("A1234567"); // Should remain unchanged

        verify(studentRepository).findById(1L);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void updateStudent_WithInvalidId_ShouldThrowException() {
        // Given
        Student updateData = new Student();
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.updateStudent(999L, updateData))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student not found with id: 999");

        verify(studentRepository).findById(999L);
        verify(studentRepository, never()).save(any());
    }

    @Test
    void deleteStudent_WithValidId_ShouldDeleteStudent() {
        // Given
        when(studentRepository.existsById(1L)).thenReturn(true);

        // When
        studentService.deleteStudent(1L);

        // Then
        verify(studentRepository).existsById(1L);
        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteStudent_WithInvalidId_ShouldThrowException() {
        // Given
        when(studentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> studentService.deleteStudent(999L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student not found with id: 999");

        verify(studentRepository).existsById(999L);
        verify(studentRepository, never()).deleteById(anyLong());
    }

    @Test
    void suspendStudent_ShouldChangeStatusToSuspended() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Student result = studentService.suspendStudent(1L);

        // Then
        assertThat(result.getStatus()).isEqualTo(StudentStatus.SUSPENDED);
        verify(studentRepository).findById(1L);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void activateStudent_ShouldChangeStatusToActive() {
        // Given
        testStudent.setStatus(StudentStatus.SUSPENDED);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Student result = studentService.activateStudent(1L);

        // Then
        assertThat(result.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        verify(studentRepository).findById(1L);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void graduateStudent_ShouldChangeStatusToGraduated() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Student result = studentService.graduateStudent(1L);

        // Then
        assertThat(result.getStatus()).isEqualTo(StudentStatus.GRADUATED);
        verify(studentRepository).findById(1L);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void graduateStudent_WithInvalidId_ShouldThrowException() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.graduateStudent(999L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student not found with id: 999");
    }

    @Test
    void findActiveStudents_ShouldReturnOnlyActiveStudents() {
        // Given
        List<Student> activeStudents = Arrays.asList(testStudent);
        when(studentRepository.findByStatus(StudentStatus.ACTIVE)).thenReturn(activeStudents);

        // When
        List<Student> result = studentService.findActiveStudents();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StudentStatus.ACTIVE);
        verify(studentRepository).findByStatus(StudentStatus.ACTIVE);
    }
}