package com.youthmission.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z]{3,20}$")
    private String name;

    @NotBlank
    private String church;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;
}
