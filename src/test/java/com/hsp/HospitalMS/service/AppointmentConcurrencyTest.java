package com.hsp.HospitalMS.service;

import com.hsp.HospitalMS.dtos.appointment.AppointmentCreateDto;
import com.hsp.HospitalMS.entity.Appointment;
import com.hsp.HospitalMS.entity.AppointmentStatus;
import com.hsp.HospitalMS.entity.Doctor;
import com.hsp.HospitalMS.entity.Patient;
import com.hsp.HospitalMS.entity.User;
import com.hsp.HospitalMS.repository.AppointmentRepository;
import com.hsp.HospitalMS.repository.DoctorRepository;
import com.hsp.HospitalMS.repository.PatientRepository;
import com.hsp.HospitalMS.repository.UserRepository;
import com.hsp.HospitalMS.service.appointment.AppointmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Proves that AppointmentServiceImpl.placeAppointment() is safe under concurrent requests.
 *
 * The test class is deliberately NOT @Transactional: every worker thread needs its own
 * real, committed transaction, otherwise the database lock is never exercised.
 */
@SpringBootTest
@ActiveProfiles("test")
class AppointmentConcurrencyTest {

    private static final int THREADS = 20;

    @Autowired private AppointmentService appointmentService;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private UserRepository userRepository;

    private Doctor doctor;
    private final List<String> patientEmails = new ArrayList<>();
    private final List<Patient> patients = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setName("Dr. Concurrency");
        doctor.setSpecialization("General");
        doctor = doctorRepository.save(doctor);

        for (int i = 0; i < THREADS; i++) {
            User user = new User();
            user.setName("Patient " + i);
            // unique emails so this test never collides with data from other test classes
            user.setEmail("concurrency-" + UUID.randomUUID() + "@example.com");
            user.setPassword("not-used");
            user = userRepository.save(user);
            users.add(user);

            Patient patient = new Patient();
            patient.setName("Patient " + i);
            patient.setUser(user);
            patients.add(patientRepository.save(patient));

            patientEmails.add(user.getEmail());
        }
    }

    @AfterEach
    void tearDown() {
        // delete only what this test created, so other test classes are not affected
        appointmentRepository.deleteAll(appointmentsOfThisDoctor());
        patientRepository.deleteAll(patients);
        doctorRepository.delete(doctor);
        userRepository.deleteAll(users);
    }

    @Test
    void twentyPatientsBookTheSameSlot_exactlyOneSucceeds() throws Exception {
        LocalDateTime slot = LocalDateTime.now().plusDays(1).withNano(0);

        Outcome outcome = runInParallel(i -> slot);

        assertEquals(1, outcome.success.get(), "exactly one booking must win the slot");
        assertEquals(THREADS - 1, outcome.conflict.get(), "everyone else must get a conflict");
        assertEquals(0, outcome.unexpected.get(), "no other kind of failure is acceptable");

        long scheduledForSlot = appointmentsOfThisDoctor().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                .filter(a -> a.getAppointmentTime().equals(slot))
                .count();
        assertEquals(1, scheduledForSlot, "the database must contain exactly one booking for the slot");
    }

    @Test
    void twentyPatientsBookDifferentSlots_allSucceed() throws Exception {
        // control test: the lock must not reject valid bookings
        LocalDateTime base = LocalDateTime.now().plusDays(2).withNano(0);

        Outcome outcome = runInParallel(i -> base.plusHours(i));

        assertEquals(THREADS, outcome.success.get());
        assertEquals(0, outcome.conflict.get());
        assertEquals(0, outcome.unexpected.get());
        assertEquals(THREADS, appointmentsOfThisDoctor().size());
    }

    // ---------------------------------------------------------------- helpers

    private List<Appointment> appointmentsOfThisDoctor() {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(doctor.getId()))
                .toList();
    }

    private static class Outcome {
        final AtomicInteger success = new AtomicInteger();
        final AtomicInteger conflict = new AtomicInteger();
        final AtomicInteger unexpected = new AtomicInteger();
    }

    /** Starts THREADS workers, releases them all at the same instant, and records how each booking ended. */
    private Outcome runInParallel(java.util.function.IntFunction<LocalDateTime> slotForPatient) throws Exception {
        Outcome outcome = new Outcome();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch allReady = new CountDownLatch(THREADS);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            final String email = patientEmails.get(i);
            final LocalDateTime slot = slotForPatient.apply(i);

            Callable<Void> task = () -> {
                AppointmentCreateDto dto = new AppointmentCreateDto();
                dto.setDoctorId(doctor.getId());
                dto.setAppointmentTime(slot);
                dto.setSymptoms("concurrency test");

                allReady.countDown();
                go.await();                       // all threads fire together
                try {
                    appointmentService.placeAppointment(email, dto);
                    outcome.success.incrementAndGet();
                } catch (IllegalStateException slotTaken) {
                    outcome.conflict.incrementAndGet();   // the handler maps this to HTTP 409
                } catch (Exception other) {
                    outcome.unexpected.incrementAndGet();
                    other.printStackTrace();
                }
                return null;
            };
            futures.add(pool.submit(task));
        }

        allReady.await();
        go.countDown();
        for (Future<Void> f : futures) {
            f.get(60, TimeUnit.SECONDS);
        }
        pool.shutdown();
        return outcome;
    }
}