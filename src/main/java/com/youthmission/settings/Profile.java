package com.youthmission.settings;

import com.youthmission.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String church;

    @Length(max = 50)
    private String occupation;

    @Length(min = 10, max = 11)
    @Pattern(regexp = "^[0-9]{10,11}$")
    private String phoneNumber;

    private String profileImage;
}
