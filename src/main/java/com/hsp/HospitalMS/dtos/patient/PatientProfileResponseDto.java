package com.hsp.HospitalMS.dtos.patient;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientProfileResponseDto {

    private String name;
    private String email;
    private Integer age;
    private String blood;

}