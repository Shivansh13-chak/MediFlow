
package com.hsp.HospitalMS.service.MedicalRecord;

import com.hsp.HospitalMS.dtos.MedicalRecord.MedicalRecordCreateDto;
import com.hsp.HospitalMS.dtos.MedicalRecord.MedicalRecordResponseDto;
import com.hsp.HospitalMS.entity.Doctor;
import com.hsp.HospitalMS.entity.MedicalRecord;
import com.hsp.HospitalMS.entity.Patient;
import com.hsp.HospitalMS.exception.ResourceNotFoundException;
import com.hsp.HospitalMS.repository.AppointmentRepository;
import com.hsp.HospitalMS.repository.DoctorRepository;
import com.hsp.HospitalMS.repository.MedicalRecordRepository;
import com.hsp.HospitalMS.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


    @Service
    public class MedicalRecordServiceImpl implements MedicalRecordService {

        private final MedicalRecordRepository medicalRecordRepository;
        private final DoctorRepository doctorRepository;
        private final PatientRepository patientRepository;
        private final AppointmentRepository appointmentRepository;

        public MedicalRecordServiceImpl(
                MedicalRecordRepository medicalRecordRepository,
                DoctorRepository doctorRepository,
                PatientRepository patientRepository,AppointmentRepository appointmentRepository) {

            this.medicalRecordRepository = medicalRecordRepository;
            this.doctorRepository = doctorRepository;
            this.patientRepository = patientRepository;
            this.appointmentRepository = appointmentRepository;
        }

        @Override
        public MedicalRecordResponseDto createRecord(
                String doctorEmail,
                MedicalRecordCreateDto dto) {

            Doctor doctor = doctorRepository
                    .findByUser_Email(doctorEmail)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("DOCTOR NOT FOUND"));

            Patient patient = patientRepository
                    .findById(dto.getPatientId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("PATIENT NOT FOUND"));

            boolean hasAppointment =
                    appointmentRepository.existsByDoctor_User_EmailAndPatient_Id(
                            doctorEmail,
                            patient.getId()
                    );

            if (!hasAppointment) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "You can only create medical records for patients with whom you have an appointment"
                );
            }

            MedicalRecord record = new MedicalRecord();

            record.setDoctor(doctor);
            record.setPatient(patient);
            record.setDiagnosis(dto.getDiagnosis());
            record.setTreatment(dto.getTreatment());
            record.setNotes(dto.getNotes());
            record.setCreatedAt(LocalDateTime.now());

            MedicalRecord savedRecord =
                    medicalRecordRepository.save(record);

            return mapToResponse(savedRecord);
        }

        @Override
        public Page<MedicalRecordResponseDto> getPatientRecords(
                String patientEmail , Pageable pageable) {

            Page<MedicalRecord> records =
                    medicalRecordRepository
                            .findByPatient_User_Email(patientEmail , pageable);

            return records.map(this::mapToResponse);
        }

        @Override
        public Page<MedicalRecordResponseDto> getDoctorRecords(
                String doctorEmail , Pageable pageable) {

            Page<MedicalRecord> records =
                    medicalRecordRepository
                            .findByDoctor_User_Email(doctorEmail , pageable);

            return records.map(this::mapToResponse);
        }

        private MedicalRecordResponseDto mapToResponse(
                MedicalRecord record) {

            MedicalRecordResponseDto response =
                    new MedicalRecordResponseDto();

            response.setId(record.getId());
            response.setPatientId(record.getPatient().getId());
            response.setDoctorId(record.getDoctor().getId());
            response.setDiagnosis(record.getDiagnosis());
            response.setTreatment(record.getTreatment());
            response.setNotes(record.getNotes());
            response.setCreatedAt(record.getCreatedAt());

            return response;
        }
    }


