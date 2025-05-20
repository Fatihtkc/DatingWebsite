package com.datingapp.backend.repository;

import com.datingapp.backend.model.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, Long> {

    // Email'e göre moderatörü getirir.
    Optional<Moderator> findByEmail(String email);

    // Belirli bir isme sahip moderatörleri arama (ilk ad veya soyad)
    List<Moderator> findByFullNameContainingIgnoreCase(String fullName);

    Optional<Moderator> findByPhone(String phone);

}
