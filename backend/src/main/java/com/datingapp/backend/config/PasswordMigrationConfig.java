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
    public CommandLineRunner migratePasswords(ManagerRepository manRepository, PasswordEncoder passwordEncoder){
        return args -> {
            manRepository.findAll().forEach(user -> {
                String plainPassword = user.getPassword();
                if (!isBcryptHash(plainPassword)){
                    String encodedPassword = passwordEncoder.encode(plainPassword);
                    user.setPassword(encodedPassword);
                    manRepository.save(user);
                }
            });
        };
    }

    @Bean
    public CommandLineRunner migratePasswords2(ModeratorRepository modRepository, PasswordEncoder passwordEncoder){
        return args -> {
            modRepository.findAll().forEach(user -> {
                String plainPassword = user.getPassword();
                if (!isBcryptHash(plainPassword)){  
                    String encodedPassword = passwordEncoder.encode(plainPassword);
                    user.setPassword(encodedPassword);
                    modRepository.save(user);
                }
            });
        };
    }

    @Bean
    public CommandLineRunner migratePasswords3(UserRepository UserRepository, PasswordEncoder passwordEncoder){
        return args -> {
            UserRepository.findAll().forEach(user -> {
                String plainPassword = user.getPassword();
                if (!isBcryptHash(plainPassword)){
                    String encodedPassword = passwordEncoder.encode(plainPassword);
                    user.setPassword(encodedPassword);
                    UserRepository.save(user);
                }
            });
        };
    }

    private boolean isBcryptHash(String password){
        return password != null && password.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
}
