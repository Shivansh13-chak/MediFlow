package com.hsp.HospitalMS.dtos.doctor;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DoctorResponseDto {

   private UUID id;
   private String name;
   private String specialization;
   private String licenseNumber;



}
