package com.hsp.HospitalMS.dtos.user;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {

    private String name;
    private String email;

}
