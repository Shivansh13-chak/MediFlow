package com.hsp.HospitalMS.dtos.appointment;

import com.hsp.HospitalMS.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AppointmentStatusUpdateDto {

    @NotNull(message = "Status is required")
    private AppointmentStatus status;

}
