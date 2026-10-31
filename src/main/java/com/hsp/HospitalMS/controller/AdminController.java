package com.hsp.HospitalMS.controller;

import com.hsp.HospitalMS.dtos.doctor.DoctorCreateDto;
import com.hsp.HospitalMS.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final DoctorService doctorService;

    public AdminController(DoctorService doctorService){
        this.doctorService=doctorService;
    }

    @PostMapping("/doctor")
    public void createDoctor(@Valid @RequestBody DoctorCreateDto doctorCreateDto) {

        doctorService.createDoctor(doctorCreateDto);

    }


}
