
package com.hsp.HospitalMS.controller;

import com.hsp.HospitalMS.dtos.patient.PatientProfileResponseDto;
import com.hsp.HospitalMS.dtos.patient.PatientProfileUpdateDto;
import com.hsp.HospitalMS.dtos.user.UserResponseDto;
import com.hsp.HospitalMS.dtos.user.UserUpdateDto;
import com.hsp.HospitalMS.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient/profile")
@RequiredArgsConstructor
public class PatientController {

    private final UserService userService;

    @GetMapping
    public PatientProfileResponseDto getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.getMyProfile(email);
    }

    @PutMapping
    public PatientProfileResponseDto updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody PatientProfileUpdateDto patientProfileUpdateDto) {

        String email = authentication.getName();

        return userService.updateMyProfile(email, patientProfileUpdateDto);
    }
}
