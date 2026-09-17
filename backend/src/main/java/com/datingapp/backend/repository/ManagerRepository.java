package com.datingapp.backend.repository;

import com.datingapp.backend.model.Manager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    // Email bazında manager getirme
    Optional<Manager> findByEmail(String email);

    Optional<Manager> findByPhone(String phone);

    @Query("SELECT m FROM Manager m WHERE LOWER(CONCAT(m.firstName, ' ', m.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Manager> searchByFullName(@Param("name") String name);
}
