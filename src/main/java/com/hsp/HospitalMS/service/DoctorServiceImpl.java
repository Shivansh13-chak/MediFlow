
package com.hsp.HospitalMS.service;

import com.hsp.HospitalMS.dtos.doctor.DoctorCreateDto;
import com.hsp.HospitalMS.dtos.doctor.DoctorListDto;
import com.hsp.HospitalMS.dtos.doctor.DoctorPatientDto;
import com.hsp.HospitalMS.dtos.doctor.DoctorResponseDto;
import com.hsp.HospitalMS.dtos.doctor.DoctorUpdateDto;
import com.hsp.HospitalMS.entity.Doctor;
import com.hsp.HospitalMS.entity.Role;
import com.hsp.HospitalMS.entity.RolesName;
import com.hsp.HospitalMS.entity.User;
import com.hsp.HospitalMS.repository.AppointmentRepository;
import com.hsp.HospitalMS.repository.DoctorRepository;
import com.hsp.HospitalMS.repository.RoleRepository;
import com.hsp.HospitalMS.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createDoctor(DoctorCreateDto doctorCreateDto) {
        if (userRepository.existsByEmail(doctorCreateDto.getEmail())) {
            throw new IllegalArgumentException("EMAIL ALREADY EXISTS");
        }

        Role role = roleRepository.findByName(RolesName.DOCTOR)
                .orElseThrow(() ->
                        new IllegalArgumentException("ROLE NOT FOUND"));

        User user = new User();
        user.setName(doctorCreateDto.getName());
        user.setEmail(doctorCreateDto.getEmail());
        user.setPassword(
                passwordEncoder.encode(doctorCreateDto.getPassword())
        );
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setName(doctorCreateDto.getName());
        doctor.setSpecialization(doctorCreateDto.getSpecialization());
        doctor.setLicensenumber(doctorCreateDto.getLicenseNumber());
        doctor.setUser(savedUser);

        doctorRepository.save(doctor);
    }

    @Override
    public DoctorResponseDto getDoctorProfile(String email) {
        Doctor doctor = doctorRepository.findByUser_Email(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("DOCTOR NOT FOUND"));

        DoctorResponseDto response = new DoctorResponseDto();
        response.setId(doctor.getId());
        response.setName(doctor.getName());
        response.setSpecialization(doctor.getSpecialization());
        response.setLicenseNumber(doctor.getLicensenumber());

        return response;
    }

    @Override
    public void updateDoctorProfile(
            String email,
            DoctorUpdateDto doctorUpdateDto
    ) {
        Doctor doctor = doctorRepository.findByUser_Email(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("DOCTOR NOT FOUND"));

        doctor.setName(doctorUpdateDto.getName());
        doctor.setSpecialization(doctorUpdateDto.getSpecialization());
        doctor.setLicensenumber(doctorUpdateDto.getLicenseNumber());

        doctorRepository.save(doctor);
    }

    @Override
    public List<DoctorListDto> getAllDoctors() {
        return doctorRepository.findAll().stream().map(doctor -> {
            DoctorListDto dto = new DoctorListDto();
            dto.setId(doctor.getId());
            dto.setName(doctor.getName());
            dto.setSpecialization(doctor.getSpecialization());
            return dto;
        }).toList();
    }

    @Override
    public List<DoctorPatientDto> getMyPatients(String email) {
        return appointmentRepository
                .findDistinctPatientsByDoctorEmail(email)
                .stream()
                .map(patient -> new DoctorPatientDto(
                        patient.getId(),
                        patient.getName()
                ))
                .toList();
    }
}
