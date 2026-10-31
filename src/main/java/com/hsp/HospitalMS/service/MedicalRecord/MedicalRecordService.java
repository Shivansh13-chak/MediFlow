package com.hsp.HospitalMS.service.MedicalRecord;

import com.hsp.HospitalMS.dtos.MedicalRecord.MedicalRecordCreateDto;
import com.hsp.HospitalMS.dtos.MedicalRecord.MedicalRecordResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicalRecordService {

    MedicalRecordResponseDto createRecord(
            String doctorEmail,
            MedicalRecordCreateDto dto
    );

    Page<MedicalRecordResponseDto> getPatientRecords(
            String patientEmail , Pageable pageable
    );

    Page<MedicalRecordResponseDto> getDoctorRecords(
            String doctorEmail , Pageable pageable
    );

}
