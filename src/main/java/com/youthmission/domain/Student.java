package com.youthmission.domain;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private School school;

    @ManyToOne
    private Account teacher;

    @ManyToOne
    private Account createBy;

    @OneToOne
    private Attendance attendance;

    @Column(nullable = false)
    private String studentName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    private LocalDateTime birthday;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    private String nameOfSchool;

    private String residence;

    private String parents;

    private String phoneNumberOfParents;

    @Enumerated(EnumType.STRING)
    private StudentType studentType;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String studentImage;//이미지는 varchar(255)보다 크기때문에 @Lob 표시



}
