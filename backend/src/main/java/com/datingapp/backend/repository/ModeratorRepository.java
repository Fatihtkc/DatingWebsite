package com.datingapp.backend.repository;

import com.datingapp.backend.model.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, Long> {

    // Email'e göre moderatörü getirir.
    Optional<Moderator> findByEmail(String email);

    @Query("SELECT m FROM Moderator m WHERE LOWER(CONCAT(m.firstName, ' ', m.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Moderator> searchByFullName(@Param("name") String name);

    Optional<Moderator> findByPhone(String phone);

}
