package com.hsp.HospitalMS.dtos.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AppointmentCreateDto {

    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    @NotNull(message = "Appointment time is required")
    private LocalDateTime appointmentTime;

    private String symptoms;

}
