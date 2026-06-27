package com.eduhab.config;

import com.eduhab.domain.User;
import com.eduhab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        int updated = 0;
        for (User user : userRepository.findAll()) {
            String hash = user.getPasswordHash();
            if (hash != null && !hash.startsWith("$2a$")) {
                user.setPasswordHash(passwordEncoder.encode(hash));
                userRepository.save(user);
                updated++;
            }
        }
        if (updated > 0) {
            log.info("Migrated {} plaintext passwords to BCrypt", updated);
        }
    }
}
