package com.hsp.HospitalMS.dtos.MedicalRecord;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MedicalRecordResponseDto {

    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private String diagnosis;
    private String treatment;
    private String notes;
    private LocalDateTime createdAt;

}
