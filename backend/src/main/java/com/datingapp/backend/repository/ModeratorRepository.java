package com.datingapp.backend.repository;

import com.datingapp.backend.model.Moderator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, Long> {

    Optional<Moderator> findByEmail(String email);

    @Query("SELECT m FROM Moderator m WHERE LOWER(CONCAT(m.firstName, ' ', m.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Moderator> searchByFullName(@Param("name") String name, Pageable page);

    Optional<Moderator> findByPhone(String phone);

}
