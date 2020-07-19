package com.youthmission.student;

import com.youthmission.account.AccountRepository;
import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import com.youthmission.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final AccountRepository accountRepository;

    public Student createStudent(Student student, School school, Account account) {
        student.setCreateBy(account);
        student.setCreatedDateTime(LocalDateTime.now());
        student.setSchool(school);
        return studentRepository.save(student);

    }
}
