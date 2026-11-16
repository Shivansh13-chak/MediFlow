
package com.hsp.HospitalMS.config;

import com.hsp.HospitalMS.entity.Role;
import com.hsp.HospitalMS.entity.RolesName;
import com.hsp.HospitalMS.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        for (RolesName roleName : RolesName.values()) {

            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .name(roleName)
                                    .build()
                    ));
        }

        System.out.println("Default roles initialized successfully.");
    }
}
