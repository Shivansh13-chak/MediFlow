package com.hsp.HospitalMS.controller;


import com.hsp.HospitalMS.dtos.MedicalRecord.MedicalRecordCreateDto;
import com.hsp.HospitalMS.dtos.MedicalRecord.MedicalRecordResponseDto;
import com.hsp.HospitalMS.service.MedicalRecord.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public MedicalRecordResponseDto createRecord(
            Authentication authentication, @Valid
            @RequestBody MedicalRecordCreateDto dto) {

        return medicalRecordService.createRecord(
                authentication.getName(),
                dto
        );
    }

    @GetMapping("/my")
    public Page<MedicalRecordResponseDto> getMyRecords(
            Authentication authentication , Pageable pageable) {

        return medicalRecordService.getPatientRecords(
                authentication.getName() , pageable
        );
    }

    @GetMapping("/doctor")
    public Page<MedicalRecordResponseDto> getDoctorRecords(
            Authentication authentication , Pageable pageable) {

        return medicalRecordService.getDoctorRecords(
                authentication.getName() , pageable
        );
    }


}
