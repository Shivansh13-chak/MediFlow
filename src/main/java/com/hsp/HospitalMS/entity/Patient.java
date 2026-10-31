package com.hsp.HospitalMS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Age")
    private Integer age;

    @Column(name = "Blood")
    private String blood;

    @Column(name = "Prescription")
    private String prescription;

    @Column(name = "Dose")
    private String dose;

    @Column(name = "fees")
    private String fees;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
