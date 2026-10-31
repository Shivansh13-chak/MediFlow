
package com.hsp.HospitalMS.service;

import com.hsp.HospitalMS.dtos.patient.PatientProfileResponseDto;
import com.hsp.HospitalMS.dtos.patient.PatientProfileUpdateDto;
import com.hsp.HospitalMS.dtos.user.UserResponseDto;
import com.hsp.HospitalMS.dtos.user.UserSignupDto;
import com.hsp.HospitalMS.dtos.user.UserUpdateDto;

public interface UserService {

    UserResponseDto createuser(UserSignupDto userSignupDto);

    PatientProfileResponseDto getMyProfile(String email);

    PatientProfileResponseDto updateMyProfile(
            String email,
            PatientProfileUpdateDto patientProfileUpdateDto
    );





}
