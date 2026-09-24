package com.datingapp.backend.service;

import org.springframework.security.core.userdetails.*;

public interface CustomUserDetailsService{
    UserDetails loadUserByUsername(String email);
}
