package com.mtg.orders.adapters.out.persistence;

import com.mtg.orders.domain.model.UserAccount;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.findByUsername("admin").isEmpty()) {
            var u = new UserAccount("admin", passwordEncoder.encode("admin123"), "ROLE_USER,ROLE_ADMIN");
            userRepository.save(u);
        }
    }
}
