package com.youthmission.settings;

import com.youthmission.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
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

    public Profile(Account account){
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.church = account.getChurch();
        this.occupation = account.getOccupation();
        this.phoneNumber = account.getPhoneNumber();
        this.profileImage = account.getProfileImage();
    }
}
