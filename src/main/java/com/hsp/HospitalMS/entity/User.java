package com.hsp.HospitalMS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false)
    private UUID id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "user_email", unique = true, nullable = false)
    private String email;
    @Column(name = "user_password")
    private String password;
    private Instant createdAt;
    private Instant updatedAt;


    @Enumerated(EnumType.STRING)
    private Provider provider= Provider.LOCAL;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id") , inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles=new HashSet<>();

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
if(createdAt==null) createdAt=now;
updatedAt=now;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt= Instant.now();


    }




}







