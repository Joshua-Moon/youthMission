package com.youthmission.domain;

import lombok.*;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Attendance {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Student student;

    private boolean isAttendant;

    private boolean isOnTime;

    private boolean afterSchool;

    @Min(0)
    private Integer qt;

    @Min(0)
    private Integer bonus;

    @Min(0)
    private Integer talent;

    private String comment;
}
