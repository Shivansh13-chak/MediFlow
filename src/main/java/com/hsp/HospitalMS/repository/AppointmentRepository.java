package com.hsp.HospitalMS.repository;

import com.hsp.HospitalMS.entity.Appointment;
import com.hsp.HospitalMS.entity.AppointmentStatus;
import com.hsp.HospitalMS.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Page<Appointment> findByPatient_User_Email(String email, Pageable pageable);
    Page<Appointment> findByDoctor_User_Email(String email, Pageable pageable);


    @Query("""
    SELECT DISTINCT a.patient
    FROM Appointment a
    WHERE a.doctor.user.email = :email
    ORDER BY a.patient.name
    """)
    List<Patient> findDistinctPatientsByDoctorEmail(@Param("email") String email);

    boolean existsByDoctor_User_EmailAndPatient_Id(
            String email,
            UUID patientId
    );

    boolean existsByDoctor_IdAndAppointmentTimeAndStatus(
            UUID doctorId,
            LocalDateTime appointmentTime,
            AppointmentStatus status
    );



}
