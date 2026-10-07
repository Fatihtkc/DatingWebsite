package com.datingapp.backend.service.impl;

import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.CustomUserDetailsService;
import com.datingapp.backend.repository.ModeratorRepository;
import com.datingapp.backend.repository.ManagerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService, UserDetailsService {

    private final  UserRepository userRepository;
    private final  ModeratorRepository moderatorRepository;
    private final  ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .map(u -> new CustomUserDetails(u.getId(), u.getEmail(), u.getPassword(), u.getRole(), u.isBanned()))
            .or(() -> moderatorRepository.findByEmail(email)
                .map(m -> new CustomUserDetails(m.getId(), m.getEmail(), m.getPassword(), m.getRole(), false)))
            .or(() -> managerRepository.findByEmail(email)
                .map(m -> new CustomUserDetails(m.getId(), m.getEmail(), m.getPassword(), m.getRole(), false)))
            .orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
    }
}
