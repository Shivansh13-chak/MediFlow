package com.hsp.HospitalMS.repository;

import com.hsp.HospitalMS.entity.Role;
import com.hsp.HospitalMS.entity.RolesName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RolesName name);
}
