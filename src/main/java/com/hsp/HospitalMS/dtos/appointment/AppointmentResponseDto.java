package com.hsp.HospitalMS.dtos.appointment;

import com.hsp.HospitalMS.entity.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AppointmentResponseDto {

    private UUID id;
    private UUID doctorId;
    private UUID patientId;
private LocalDateTime appointmentTime;
private String symptoms;
private AppointmentStatus status;



}
