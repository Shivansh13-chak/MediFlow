package com.hsp.HospitalMS.controller;

import com.hsp.HospitalMS.dtos.appointment.AppointmentCreateDto;
import com.hsp.HospitalMS.dtos.appointment.AppointmentResponseDto;
import com.hsp.HospitalMS.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("appointments")
public class AppointmentController {


    private final AppointmentService appointmentService;


    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public AppointmentResponseDto placeAppointment( Authentication authentication,
           @Valid @RequestBody AppointmentCreateDto appointmentCreateDto) {

        return appointmentService.placeAppointment( authentication.getName(),appointmentCreateDto);
    }

    @GetMapping("/my")
    public Page<AppointmentResponseDto> getMyAppointments(
            Authentication authentication , Pageable pageable) {

        return appointmentService.getMyAppointments(
                authentication.getName() , pageable
        );
    }

    @PutMapping("/{appointmentId}/cancel")
    public void cancelAppointment(
            Authentication authentication,
            @PathVariable UUID appointmentId) {

        appointmentService.cancelAppointment(
                appointmentId,
                authentication.getName()
        );
    }





}
