package com.youthmission.school.validator;


import com.youthmission.school.SchoolRepository;
import com.youthmission.school.form.SchoolForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SchoolFormValidator implements Validator {

    private final SchoolRepository schoolRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return SchoolForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SchoolForm schoolForm = (SchoolForm)target;
        if (schoolRepository.existsByPath(schoolForm.getPath())) {
            errors.rejectValue("path", "wrong.path", "해당 교회학교 경로값을 사용할 수 없습니다.");
        }
    }
}
