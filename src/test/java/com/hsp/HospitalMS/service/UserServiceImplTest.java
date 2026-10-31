
package com.hsp.HospitalMS.service;

import com.hsp.HospitalMS.dtos.patient.PatientProfileResponseDto;
import com.hsp.HospitalMS.dtos.patient.PatientProfileUpdateDto;
import com.hsp.HospitalMS.entity.Patient;
import com.hsp.HospitalMS.entity.User;
import com.hsp.HospitalMS.exception.ResourceNotFoundException;
import com.hsp.HospitalMS.mapper.UserMapper;
import com.hsp.HospitalMS.repository.PatientRepository;
import com.hsp.HospitalMS.repository.RoleRepository;
import com.hsp.HospitalMS.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getMyProfile_returnsUserDetails() {
        User user = new User();
        user.setName("Shivansh");
        user.setEmail("shivan@example.com");

        Patient patient = new Patient();
        patient.setAge(21);
        patient.setBlood("B+");
        patient.setUser(user);

        when(userRepository.findByEmail("shivan@example.com"))
                .thenReturn(Optional.of(user));

        when(patientRepository.findByUser_Email("shivan@example.com"))
                .thenReturn(Optional.of(patient));

        PatientProfileResponseDto result =
                userService.getMyProfile("shivan@example.com");

        assertEquals("Shivansh", result.getName());
        assertEquals("shivan@example.com", result.getEmail());
        assertEquals(21, result.getAge());
        assertEquals("B+", result.getBlood());
    }

    @Test
    void getMyProfile_throwsExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getMyProfile("missing@example.com")
        );
    }

    @Test
    void updateMyProfile_updatesNameAgeAndBlood() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("shivan@example.com");

        Patient patient = new Patient();
        patient.setAge(20);
        patient.setBlood("A+");
        patient.setUser(user);

        when(userRepository.findByEmail("shivan@example.com"))
                .thenReturn(Optional.of(user));

        when(patientRepository.findByUser_Email("shivan@example.com"))
                .thenReturn(Optional.of(patient));

        when(userRepository.save(user)).thenReturn(user);
        when(patientRepository.save(patient)).thenReturn(patient);

        PatientProfileUpdateDto updateDto = new PatientProfileUpdateDto();
        updateDto.setName("New Name");
        updateDto.setAge(21);
        updateDto.setBlood("B+");

        PatientProfileResponseDto result =
                userService.updateMyProfile("shivan@example.com", updateDto);

        assertEquals("New Name", result.getName());
        assertEquals("shivan@example.com", result.getEmail());
        assertEquals(21, result.getAge());
        assertEquals("B+", result.getBlood());

        verify(userRepository).save(user);
        verify(patientRepository).save(patient);
    }

    @Test
    void createuser_rejectsDuplicateEmail() {
        com.hsp.HospitalMS.dtos.user.UserSignupDto signupDto =
                new com.hsp.HospitalMS.dtos.user.UserSignupDto();

        signupDto.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createuser(signupDto)
        );

        verify(userRepository, never()).save(any(User.class));
    }
}
