package com.youthmission.student;

import com.youthmission.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudentRepository extends JpaRepository<Student, Long> {
}
