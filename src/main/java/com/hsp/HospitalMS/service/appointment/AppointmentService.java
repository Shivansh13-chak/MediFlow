package com.hsp.HospitalMS.service.appointment;

import com.hsp.HospitalMS.dtos.appointment.AppointmentCreateDto;
import com.hsp.HospitalMS.dtos.appointment.AppointmentResponseDto;
import com.hsp.HospitalMS.dtos.appointment.AppointmentStatusUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    AppointmentResponseDto placeAppointment(String email , AppointmentCreateDto appointmentCreateDto);

    Page<AppointmentResponseDto> getMyAppointments(String email, Pageable pageable);

    Page<AppointmentResponseDto> getDoctorAppointments(String email, Pageable pageable);

    void statusUpdate(UUID appointmentId, String email, AppointmentStatusUpdateDto dto);
    void cancelAppointment(UUID appointmentId, String email);


}
