package com.youthmission.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    private LocalDateTime joinAt; //가입날

    //이하 프로필관련
    private String bio; //짧은 자기소개

    private String url;

    private String occupation;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;//이미지는 varchar(255)보다 크기때문에 @Lob 표시

    //이하 알림설정
    private boolean studyCreatedByEmail;//스터디모임 생성되면 이메일로 알림을 받을 것인가?

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;//스터디모임 가입 결과를 이메일로 받을 것인가?

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail; //스터디모임 내용 변경을 이메일로 받을 것인가?

    private boolean studyUpdatedByWeb;


    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public boolean isValidToken(String token){
        return this.emailCheckToken.equals(token);
    }

    public void completeSignUp() {
        this.setEmailVerified(true);
        this.setJoinAt(LocalDateTime.now());
    }
}