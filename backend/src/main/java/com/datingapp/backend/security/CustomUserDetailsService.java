package com.datingapp.backend.security;

import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.UserRepository;

import lombok.NoArgsConstructor;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@NoArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .map(u -> new CustomUserDetails(u.getId(), u.getEmail(), u.getPassword(), u.getRole()))
            .or(() -> moderatorRepository.findByEmail(email)
                .map(m -> new CustomUserDetails(m.getId(), m.getEmail(), m.getPassword(), m.getRole())))
            .or(() -> managerRepository.findByEmail(email)
                .map(m -> new CustomUserDetails(m.getId(), m.getEmail(), m.getPassword(), m.getRole())))
            .orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
    }
}
