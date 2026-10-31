package com.hsp.HospitalMS.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true,nullable = false)
    @Enumerated(EnumType.STRING)
    private RolesName name;


}
