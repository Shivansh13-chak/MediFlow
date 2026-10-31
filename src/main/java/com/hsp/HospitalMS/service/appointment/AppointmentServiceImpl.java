package com.hsp.HospitalMS.service.appointment;

import com.hsp.HospitalMS.dtos.appointment.AppointmentCreateDto;
import com.hsp.HospitalMS.dtos.appointment.AppointmentResponseDto;
import com.hsp.HospitalMS.dtos.appointment.AppointmentStatusUpdateDto;
import com.hsp.HospitalMS.entity.Appointment;
import com.hsp.HospitalMS.entity.AppointmentStatus;
import com.hsp.HospitalMS.entity.Doctor;
import com.hsp.HospitalMS.entity.Patient;
import com.hsp.HospitalMS.repository.AppointmentRepository;
import com.hsp.HospitalMS.repository.DoctorRepository;
import com.hsp.HospitalMS.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

AppointmentServiceImpl(DoctorRepository doctorRepository,PatientRepository patientRepository,AppointmentRepository appointmentRepository)
{
    this.appointmentRepository=appointmentRepository;
    this.patientRepository=patientRepository;
    this.doctorRepository=doctorRepository;
}


    @Override
    @Transactional
    public AppointmentResponseDto placeAppointment(
            String email,
            AppointmentCreateDto appointmentCreateDto) {

        Doctor doctor = doctorRepository
                .findByIdForUpdate(appointmentCreateDto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("DOCTOR NOT FOUND"));

        Patient patient = patientRepository
                .findByUser_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("PATIENT NOT FOUND"));

        boolean slotBooked = appointmentRepository
                .existsByDoctor_IdAndAppointmentTimeAndStatus(
                        doctor.getId(),
                        appointmentCreateDto.getAppointmentTime(),
                        AppointmentStatus.SCHEDULED
                );

        if (slotBooked) {
            throw new IllegalStateException(
                    "This appointment slot is already booked. Please select another time."
            );
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(appointmentCreateDto.getAppointmentTime());
        appointment.setSymptoms(appointmentCreateDto.getSymptoms());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        AppointmentResponseDto response = new AppointmentResponseDto();
        response.setId(savedAppointment.getId());
        response.setDoctorId(savedAppointment.getDoctor().getId());
        response.setPatientId(savedAppointment.getPatient().getId());
        response.setAppointmentTime(savedAppointment.getAppointmentTime());
        response.setSymptoms(savedAppointment.getSymptoms());
        response.setStatus(savedAppointment.getStatus());

        return response;
    }

    @Override
    public Page<AppointmentResponseDto> getMyAppointments(
            String email,
            Pageable pageable) {

        Page<Appointment> appointments =
                appointmentRepository
                        .findByPatient_User_Email(email, pageable);

        return appointments.map(appointment -> {

            AppointmentResponseDto response =
                    new AppointmentResponseDto();

            response.setId(appointment.getId());
            response.setDoctorId(
                    appointment.getDoctor().getId());
            response.setPatientId(
                    appointment.getPatient().getId());
            response.setAppointmentTime(
                    appointment.getAppointmentTime());
            response.setSymptoms(
                    appointment.getSymptoms());
            response.setStatus(
                    appointment.getStatus());

            return response;
        });
    }

    @Override
    public Page<AppointmentResponseDto> getDoctorAppointments(
            String email,
            Pageable pageable) {

        Page<Appointment> appointments =
                appointmentRepository
                        .findByDoctor_User_Email(email, pageable);

        return appointments.map(appointment -> {

            AppointmentResponseDto response =
                    new AppointmentResponseDto();

            response.setId(appointment.getId());
            response.setDoctorId(
                    appointment.getDoctor().getId());
            response.setPatientId(
                    appointment.getPatient().getId());
            response.setAppointmentTime(
                    appointment.getAppointmentTime());
            response.setSymptoms(
                    appointment.getSymptoms());
            response.setStatus(
                    appointment.getStatus());

            return response;
        });
    }

    @Override
  public void statusUpdate(UUID appointmentId, String email, AppointmentStatusUpdateDto dto){

         Appointment appointment= appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("APPOINTMENT NOT FOUND"));

        if(!appointment.getDoctor().getUser().getEmail().equals(email))
        {
            throw new IllegalArgumentException("DOCTOR NOT AUTHORIZED");
        }

        appointment.setStatus(dto.getStatus());
        appointmentRepository.save(appointment);

    }

    @Override
    public void cancelAppointment(UUID appointmentId, String email) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("APPOINTMENT NOT FOUND"));

        if (!appointment.getPatient().getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("PATIENT NOT AUTHORIZED");
        }
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new IllegalArgumentException("ONLY SCHEDULED APPOINTMENTS CAN BE CANCELLED");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.save(appointment);
}


}













