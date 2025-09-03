package com.example.crud_api.student;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface StudentRepository extends JpaRepository< Student, Long> {

}

