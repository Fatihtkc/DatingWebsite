package com.datingapp.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.datingapp.backend.repository.ManagerRepository;
import com.datingapp.backend.repository.ModeratorRepository;
import com.datingapp.backend.repository.UserRepository;

@Configuration
public class PasswordMigrationConfig {
        
    @Bean
    public CommandLineRunner migratePasswords(ManagerRepository manRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Tüm kullanıcıları çekiyoruz
            manRepository.findAll().forEach(user -> {
                String plainPassword = user.getPassword();
                // Eğer şifre hali hazırda encode edilmiş değilse (örneğin, belli bir deseni kontrol edebilirsiniz)
                if (!plainPassword.startsWith("$2a$")) {  // BCrypt hash'leri "$2a$" ile başlar
                    String encodedPassword = passwordEncoder.encode(plainPassword);
                    user.setPassword(encodedPassword);
                    manRepository.save(user);
                    System.out.println("Updated password for user: " + user.getEmail());
                }
            });
        };
    }

    @Bean
    public CommandLineRunner migratePasswords2(ModeratorRepository modRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Tüm kullanıcıları çekiyoruz
            modRepository.findAll().forEach(user -> {
                String plainPassword = user.getPassword();
                // Eğer şifre hali hazırda encode edilmiş değilse (örneğin, belli bir deseni kontrol edebilirsiniz)
                if (!plainPassword.startsWith("$2a$")) {  // BCrypt hash'leri "$2a$" ile başlar
                    String encodedPassword = passwordEncoder.encode(plainPassword);
                    user.setPassword(encodedPassword);
                    modRepository.save(user);
                    System.out.println("Updated password for user: " + user.getEmail());
                }
            });
        };
    }

    @Bean
    public CommandLineRunner migratePasswords3(UserRepository UserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Tüm kullanıcıları çekiyoruz
            UserRepository.findAll().forEach(user -> {
                String plainPassword = user.getPassword();
                // Eğer şifre hali hazırda encode edilmiş değilse (örneğin, belli bir deseni kontrol edebilirsiniz)
                if (!plainPassword.startsWith("$2a$")) {  // BCrypt hash'leri "$2a$" ile başlar
                    String encodedPassword = passwordEncoder.encode(plainPassword);
                    user.setPassword(encodedPassword);
                    UserRepository.save(user);
                    System.out.println("Updated password for user: " + user.getEmail());
                }
            });
        };
    }
}
