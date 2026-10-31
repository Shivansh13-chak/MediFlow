
package com.hsp.HospitalMS.controller;

import com.hsp.HospitalMS.dtos.appointment.AppointmentResponseDto;
import com.hsp.HospitalMS.dtos.appointment.AppointmentStatusUpdateDto;
import com.hsp.HospitalMS.dtos.doctor.DoctorPatientDto;
import com.hsp.HospitalMS.dtos.doctor.DoctorResponseDto;
import com.hsp.HospitalMS.dtos.doctor.DoctorUpdateDto;
import com.hsp.HospitalMS.service.DoctorService;
import com.hsp.HospitalMS.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(
            DoctorService doctorService,
            AppointmentService appointmentService
    ) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/profile")
    public DoctorResponseDto doctorProfile(Authentication authentication) {
        String email = authentication.getName();
        return doctorService.getDoctorProfile(email);
    }

    @PutMapping("/profile")
    public void updateProfile(
            Authentication authentication,
            @Valid @RequestBody DoctorUpdateDto doctorUpdateDto
    ) {
        doctorService.updateDoctorProfile(
                authentication.getName(),
                doctorUpdateDto
        );
    }

    @GetMapping("/appointments")
    public Page<AppointmentResponseDto> getDoctorAppointments(
            Authentication authentication,
            Pageable pageable
    ) {
        String email = authentication.getName();
        return appointmentService.getDoctorAppointments(email, pageable);
    }

    @PutMapping("/appointment/{appointmentId}/status")
    public void statusUpdate(
            Authentication authentication,
            @PathVariable UUID appointmentId,
            @Valid @RequestBody AppointmentStatusUpdateDto dto
    ) {
        String email = authentication.getName();
        appointmentService.statusUpdate(appointmentId, email, dto);
    }

    // Returns only patients who have appointments with the logged-in doctor.
    @GetMapping("/patients")
    public List<DoctorPatientDto> getMyPatients(
            Authentication authentication
    ) {
        String email = authentication.getName();
        return doctorService.getMyPatients(email);
    }
}
