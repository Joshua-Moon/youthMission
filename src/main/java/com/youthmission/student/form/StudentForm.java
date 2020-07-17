package com.youthmission.student.form;

import com.youthmission.domain.Account;
import com.youthmission.domain.Gender;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class StudentForm {

    @NotBlank
    @Length(max = 20)
    private String studentName;

    private Gender gender = Gender.MALE;

    @Length(min = 10, max = 11)
    @Pattern(regexp = "^[0-9]{10,11}$")
    private String phoneNumber;

    private Account teacher;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime birthday;

    private String nameOfSchool;

    private String residence;

    private String parents;

    @Length(min = 10, max = 11)
    @Pattern(regexp = "^[0-9]{10,11}$")
    private String phoneNumberOfParents;

    private String profileImage;

}
