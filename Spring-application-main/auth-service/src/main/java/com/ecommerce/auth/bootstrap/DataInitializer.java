package com.ecommerce.auth.bootstrap;

import com.ecommerce.auth.domain.Role;
import com.ecommerce.auth.domain.User;
import com.ecommerce.auth.repo.RoleRepository;
import com.ecommerce.auth.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role userRole =
                roleRepository
                        .findByName("ROLE_USER")
                        .orElseGet(
                                () -> {
                                    Role r = new Role();
                                    r.setName("ROLE_USER");
                                    return roleRepository.save(r);
                                });
        Role adminRole =
                roleRepository
                        .findByName("ROLE_ADMIN")
                        .orElseGet(
                                () -> {
                                    Role r = new Role();
                                    r.setName("ROLE_ADMIN");
                                    return roleRepository.save(r);
                                });

        if (userRepository.findByEmailIgnoreCase(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setEnabled(true);
            admin.getRoles().add(userRole);
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            log.info("Seeded admin user {}", adminEmail);
        }
    }
}
