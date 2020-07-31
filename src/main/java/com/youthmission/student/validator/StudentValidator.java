package com.youthmission.student.validator;

import com.youthmission.student.form.StudentForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class StudentValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return StudentForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudentForm studentForm = (StudentForm)target;

//        if (isNotValidEndEnrollmentDateTime(studentForm)) {
//            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 접수 종료 일시를 정확히 입력하세요.");
//        }
//
//        if (isNotValidEndDateTime(studentForm)) {
//            errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
//        }
//
//        if (isNotValidStartDateTime(studentForm)) {
//            errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력하세요.");
//        }
    }

    private boolean isNotValidBirthday(StudentForm studentForm) {
        return studentForm.getBirthday().isBefore(LocalDate.now());
    }

}
