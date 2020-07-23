package com.youthmission.student;

import com.youthmission.domain.School;
import com.youthmission.domain.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findBySchoolOrderByStudentName(School school);
}
