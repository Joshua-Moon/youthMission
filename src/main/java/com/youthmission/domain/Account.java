package com.youthmission.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    //이하 로그인 관련
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private String password;

    private String church;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt; //가입날

    //이하 프로필관련
    private String bio; //짧은 자기소개

    private String url;

    private String occupation;

    private String phoneNumber;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;//이미지는 varchar(255)보다 크기때문에 @Lob 표시

    //이하 알림설정
    private boolean umionCreatedByEmail;//유미온 생성되면 이메일로 알림을 받을 것인가?

    private boolean umionCreatedByWeb;

    private boolean umionEnrollmentResultByEmail;//유미온 가입 결과를 이메일로 받을 것인가?

    private boolean umionEnrollmentResultByWeb;

    private boolean umionUpdatedByEmail; //유미온 내용 변경을 이메일로 받을 것인가?

    private boolean umionUpdatedByWeb;

    @ManyToMany
    private Set<Tag> tags;

    @OneToMany(mappedBy = "teacher")
    private Set<Student> students = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }
    public boolean isValidToken(String token){
        return this.emailCheckToken.equals(token);
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    public boolean isManagerOf(School school) {
        return school.getManagers().contains(this);
    }

    public String nameEmail() {
        return this.getName() + "(" + this.getEmail() + ")";
    }
}
