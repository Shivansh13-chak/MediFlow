package com.hsp.HospitalMS.service;

import com.hsp.HospitalMS.dtos.doctor.*;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DoctorService {

    public void createDoctor(DoctorCreateDto doctorCreateDto);


    DoctorResponseDto getDoctorProfile(String email);

    void updateDoctorProfile(String email, DoctorUpdateDto doctorUpdateDto);

    List<DoctorListDto> getAllDoctors();

    List<DoctorPatientDto> getMyPatients(String email);

}
