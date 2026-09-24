package com.datingapp.backend.service.impl;

import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.CustomUserDetailsService;
import com.datingapp.backend.repository.ModeratorRepository;
import com.datingapp.backend.repository.ManagerRepository;


import lombok.NoArgsConstructor;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService, UserDetailsService {

    private UserRepository userRepository;
    private ModeratorRepository moderatorRepository;
    private ManagerRepository managerRepository;

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
