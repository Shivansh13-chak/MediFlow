package com.hsp.HospitalMS.controller;

import com.hsp.HospitalMS.dtos.doctor.DoctorListDto;
import com.hsp.HospitalMS.service.DoctorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorDirectoryController {

    private final DoctorService doctorService;

    public DoctorDirectoryController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<DoctorListDto> getAllDoctors() {
        return doctorService.getAllDoctors();
    }
}