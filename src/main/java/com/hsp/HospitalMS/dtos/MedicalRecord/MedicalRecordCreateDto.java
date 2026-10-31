package com.hsp.HospitalMS.dtos.MedicalRecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
    @Setter
    public class MedicalRecordCreateDto {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    @NotBlank(message = "Treatment is required")
    private String treatment;

    private String notes;

    }


