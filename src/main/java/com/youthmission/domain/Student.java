package com.youthmission.domain;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private School school;

    @ManyToOne
    private Account createBy;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @ManyToOne
    private Account teacher;

    @OneToMany(mappedBy = "student" ) //TODO 양쪽 맵핑이 더 효과적일지 단뱡향이 더 효과적일지 판단하자
    private List<Attendance> attendance = new ArrayList<>();

    @Column(nullable = false)
    private String studentName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    private LocalDate birthday;

    private String nameOfSchool;

    private String residence;

    private String parents;

    private String phoneNumberOfParents;

    @Enumerated(EnumType.STRING) //TODO 그냥 출석률 퍼센트로 표시하는건 어떤가?
    private StudentType studentType;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String studentImage;//이미지는 varchar(255)보다 크기때문에 @Lob 표시



}
