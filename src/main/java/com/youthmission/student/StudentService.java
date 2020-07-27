package com.youthmission.student;

import com.youthmission.account.AccountRepository;
import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import com.youthmission.domain.Student;
import com.youthmission.student.form.StudentForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;


    public Student createStudent(Student student, School school, Account account) {
        student.setCreateBy(account);
        student.setCreatedDateTime(LocalDateTime.now());
        student.setSchool(school);
        return studentRepository.save(student);

    }

    public void updateStudent(Student student, StudentForm studentForm) {
        modelMapper.map(studentForm, student);
        studentRepository.save(student);
    }

    public String getTeacherEmail(StudentForm studentForm) {
        String teacherNameEmail = studentForm.getTeacherNameEmail();
        int start = teacherNameEmail.indexOf("(");
        int end = teacherNameEmail.indexOf(")", start+1);
        return teacherNameEmail.substring(start+1, end);
    }

    public void deleteEvent(Student student) {
        studentRepository.delete(student);
    }
}
