package com.datingapp.backend.repository;

import com.datingapp.backend.model.Manager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    // Email bazında manager getirme
    Optional<Manager> findByEmail(String email);
    Optional<Manager> findByPhone(String phone);

    // Belirli bir isme sahip manager arama
    List<Manager> findByFullNameContainingIgnoreCase(String fullName);
}
