package com.hsp.HospitalMS.repository;

import com.hsp.HospitalMS.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {

    Page<MedicalRecord> findByPatient_User_Email(String email , Pageable pageable);

    Page<MedicalRecord> findByDoctor_User_Email(String email , Pageable pageable);
}
