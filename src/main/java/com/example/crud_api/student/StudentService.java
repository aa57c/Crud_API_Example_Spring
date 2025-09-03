package com.example.crud_api.student;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Student> findStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student createStudent(Student student) {
        // Set enrollment date if not provided
        if (student.getEnrollmentDate() == null) {
            student.setEnrollmentDate(LocalDateTime.now());
        }

        // Ensure new student is active
        student.setStatus(StudentStatus.ACTIVE);
        student.setId(null); // Ensure it's a new entity

        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student studentData) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        // Update only the fields that should be updatable
        existingStudent.setName(studentData.getName());
        existingStudent.setAge(studentData.getAge());
        existingStudent.setEmail(studentData.getEmail());
        existingStudent.setGraduationYear(studentData.getGraduationYear());

        // Don't update: passport number (immutable), enrollment date, audit fields

        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public Student suspendStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        student.suspend();
        return studentRepository.save(student);
    }

    public Student activateStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        student.activate();
        return studentRepository.save(student);
    }

    public Student graduateStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        student.graduate();
        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public List<Student> findActiveStudents() {
        return studentRepository.findByStatus(StudentStatus.ACTIVE);
    }
}