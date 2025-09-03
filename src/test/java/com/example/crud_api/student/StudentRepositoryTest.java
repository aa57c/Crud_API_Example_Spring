package com.example.crud_api.student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    private Student activeStudent;
    private Student suspendedStudent;
    private Student graduatedStudent;

    @BeforeEach
    void setUp() {
        // Create test students
        activeStudent = new Student(
                "John Active",
                "A1111111",
                25,
                "john.active@example.com",
                LocalDateTime.now().minusMonths(6),
                2025
        );
        activeStudent.setStatus(StudentStatus.ACTIVE);

        suspendedStudent = new Student(
                "Jane Suspended",
                "B2222222",
                22,
                "jane.suspended@example.com",
                LocalDateTime.now().minusMonths(3),
                2026
        );
        suspendedStudent.setStatus(StudentStatus.SUSPENDED);

        graduatedStudent = new Student(
                "Bob Graduated",
                "C3333333",
                28,
                "bob.graduated@example.com",
                LocalDateTime.now().minusMonths(12),
                2024
        );
        graduatedStudent.setStatus(StudentStatus.GRADUATED);

        // Persist test data
        entityManager.persistAndFlush(activeStudent);
        entityManager.persistAndFlush(suspendedStudent);
        entityManager.persistAndFlush(graduatedStudent);
    }

    @Test
    void findByStatus_WithActiveStatus_ShouldReturnOnlyActiveStudents() {
        // When
        List<Student> activeStudents = studentRepository.findByStatus(StudentStatus.ACTIVE);

        // Then
        assertThat(activeStudents).hasSize(1);
        assertThat(activeStudents.get(0).getName()).isEqualTo("John Active");
        assertThat(activeStudents.get(0).getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    void findByStatus_WithSuspendedStatus_ShouldReturnOnlySuspendedStudents() {
        // When
        List<Student> suspendedStudents = studentRepository.findByStatus(StudentStatus.SUSPENDED);

        // Then
        assertThat(suspendedStudents).hasSize(1);
        assertThat(suspendedStudents.get(0).getName()).isEqualTo("Jane Suspended");
        assertThat(suspendedStudents.get(0).getStatus()).isEqualTo(StudentStatus.SUSPENDED);
    }

    @Test
    void findActiveStudents_ShouldReturnOnlyActiveStudents() {
        // When
        List<Student> activeStudents = studentRepository.findActiveStudents();

        // Then
        assertThat(activeStudents).hasSize(1);
        assertThat(activeStudents.get(0).getName()).isEqualTo("John Active");
        assertThat(activeStudents.get(0).getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnStudent() {
        // When
        Optional<Student> foundStudent = studentRepository.findByEmail("john.active@example.com");

        // Then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("John Active");
        assertThat(foundStudent.get().getEmail()).isEqualTo("john.active@example.com");
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        // When
        Optional<Student> foundStudent = studentRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundStudent).isEmpty();
    }

    @Test
    void findByPassportNumber_WithExistingPassport_ShouldReturnStudent() {
        // When
        Optional<Student> foundStudent = studentRepository.findByPassportNumber("B2222222");

        // Then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("Jane Suspended");
        assertThat(foundStudent.get().getPassportNumber()).isEqualTo("B2222222");
    }

    @Test
    void findByPassportNumber_WithNonExistingPassport_ShouldReturnEmpty() {
        // When
        Optional<Student> foundStudent = studentRepository.findByPassportNumber("Z9999999");

        // Then
        assertThat(foundStudent).isEmpty();
    }

    @Test
    void findByGraduationYear_WithExistingYear_ShouldReturnStudents() {
        // When
        List<Student> students2025 = studentRepository.findByGraduationYear(2025);

        // Then
        assertThat(students2025).hasSize(1);
        assertThat(students2025.get(0).getName()).isEqualTo("John Active");
        assertThat(students2025.get(0).getGraduationYear()).isEqualTo(2025);
    }

    @Test
    void findByGraduationYear_WithNonExistingYear_ShouldReturnEmptyList() {
        // When
        List<Student> students2030 = studentRepository.findByGraduationYear(2030);

        // Then
        assertThat(students2030).isEmpty();
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        // When
        boolean exists = studentRepository.existsByEmail("john.active@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        // When
        boolean exists = studentRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByPassportNumber_WithExistingPassport_ShouldReturnTrue() {
        // When
        boolean exists = studentRepository.existsByPassportNumber("A1111111");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByPassportNumber_WithNonExistingPassport_ShouldReturnFalse() {
        // When
        boolean exists = studentRepository.existsByPassportNumber("Z9999999");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void save_NewStudent_ShouldPersistSuccessfully() {
        // Given
        Student newStudent = new Student(
                "Alice New",
                "D4444444",
                21,
                "alice.new@example.com",
                LocalDateTime.now(),
                2027
        );

        // When
        Student savedStudent = studentRepository.save(newStudent);

        // Then
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getName()).isEqualTo("Alice New");
        assertThat(savedStudent.getPassportNumber()).isEqualTo("D4444444");
        assertThat(savedStudent.getEmail()).isEqualTo("alice.new@example.com");
        assertThat(savedStudent.getStatus()).isEqualTo(StudentStatus.ACTIVE);

        // Verify it's actually in the database
        Optional<Student> retrievedStudent = studentRepository.findById(savedStudent.getId());
        assertThat(retrievedStudent).isPresent();
        assertThat(retrievedStudent.get().getName()).isEqualTo("Alice New");
    }

    @Test
    void update_ExistingStudent_ShouldUpdateSuccessfully() {
        // Given - get existing student and modify it
        Student student = entityManager.find(Student.class, activeStudent.getId());
        student.setName("John Updated");
        student.setAge(26);

        // When
        Student updatedStudent = studentRepository.save(student);

        // Then
        assertThat(updatedStudent.getName()).isEqualTo("John Updated");
        assertThat(updatedStudent.getAge()).isEqualTo(26);
        assertThat(updatedStudent.getId()).isEqualTo(activeStudent.getId());

        // Verify the change persisted
        entityManager.flush();
        entityManager.clear();

        Student retrievedStudent = entityManager.find(Student.class, activeStudent.getId());
        assertThat(retrievedStudent.getName()).isEqualTo("John Updated");
        assertThat(retrievedStudent.getAge()).isEqualTo(26);
    }

    @Test
    void delete_ExistingStudent_ShouldRemoveFromDatabase() {
        // Given
        Long studentId = activeStudent.getId();
        assertThat(studentRepository.existsById(studentId)).isTrue();

        // When
        studentRepository.deleteById(studentId);

        // Then
        assertThat(studentRepository.existsById(studentId)).isFalse();
        Optional<Student> deletedStudent = studentRepository.findById(studentId);
        assertThat(deletedStudent).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllStudents() {
        // When
        List<Student> allStudents = studentRepository.findAll();

        // Then
        assertThat(allStudents).hasSize(3);
        assertThat(allStudents)
                .extracting(Student::getName)
                .containsExactlyInAnyOrder("John Active", "Jane Suspended", "Bob Graduated");
    }

    @Test
    void findById_WithExistingId_ShouldReturnStudent() {
        // When
        Optional<Student> foundStudent = studentRepository.findById(activeStudent.getId());

        // Then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("John Active");
        assertThat(foundStudent.get().getId()).isEqualTo(activeStudent.getId());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // When
        Optional<Student> foundStudent = studentRepository.findById(99999L);

        // Then
        assertThat(foundStudent).isEmpty();
    }
}