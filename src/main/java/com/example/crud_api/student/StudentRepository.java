package com.example.crud_api.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find by status
    List<Student> findByStatus(StudentStatus status);

    // Find active students
    @Query("SELECT s FROM Student s WHERE s.status = 'ACTIVE'")
    List<Student> findActiveStudents();

    // Find by email
    Optional<Student> findByEmail(String email);

    // Find by passport number
    Optional<Student> findByPassportNumber(String passportNumber);

    // Find students by graduation year
    List<Student> findByGraduationYear(Integer graduationYear);

    // Check if email exists (useful for validation)
    boolean existsByEmail(String email);

    // Check if passport number exists
    boolean existsByPassportNumber(String passportNumber);
}