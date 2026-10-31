package com.hsp.HospitalMS.service;

import com.hsp.HospitalMS.dtos.patient.PatientProfileResponseDto;
import com.hsp.HospitalMS.dtos.patient.PatientProfileUpdateDto;
import com.hsp.HospitalMS.dtos.user.UserResponseDto;
import com.hsp.HospitalMS.dtos.user.UserSignupDto;
import com.hsp.HospitalMS.dtos.user.UserUpdateDto;
import com.hsp.HospitalMS.entity.Patient;
import com.hsp.HospitalMS.entity.Provider;
import com.hsp.HospitalMS.entity.Role;
import com.hsp.HospitalMS.entity.RolesName;
import com.hsp.HospitalMS.entity.User;
import com.hsp.HospitalMS.exception.ResourceNotFoundException;
import com.hsp.HospitalMS.mapper.UserMapper;
import com.hsp.HospitalMS.repository.PatientRepository;
import com.hsp.HospitalMS.repository.RoleRepository;
import com.hsp.HospitalMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserMapper userMapper;


    @Override
    public UserResponseDto createuser(UserSignupDto userSignupDto){
        if (userSignupDto.getEmail() == null || userSignupDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required!");
        }


        if (userRepository.existsByEmail(userSignupDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }


        User user = userMapper.toEntity(userSignupDto);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider(Provider.LOCAL);


        Role role = roleRepository.findByName(RolesName.PATIENT)
                .orElseThrow(() -> new RuntimeException("Role not found"));


        user.setRoles(Set.of(role));


        User savedUser = userRepository.save(user);

        // 8. Create and link Patient
        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setName(savedUser.getName());
        patientRepository.save(patient);


        return UserResponseDto.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();

    }

    @Override
    public PatientProfileResponseDto getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Patient patient = patientRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return PatientProfileResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .age(patient.getAge())
                .blood(patient.getBlood())
                .build();
    }

    @Override
    @Transactional
    public PatientProfileResponseDto updateMyProfile(
            String email,
            PatientProfileUpdateDto patientProfileUpdateDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Patient patient = patientRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Update User information
        user.setName(patientProfileUpdateDto.getName());

// Keep Patient name synchronized with User name
        patient.setName(patientProfileUpdateDto.getName());

// Update Patient information
        patient.setAge(patientProfileUpdateDto.getAge());
        patient.setBlood(patientProfileUpdateDto.getBlood());

        User updatedUser = userRepository.save(user);
        Patient updatedPatient = patientRepository.save(patient);

        return PatientProfileResponseDto.builder()
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .age(updatedPatient.getAge())
                .blood(updatedPatient.getBlood())
                .build();
    }



    }

