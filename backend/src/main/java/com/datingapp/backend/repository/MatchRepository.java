package com.datingapp.backend.repository;

import com.datingapp.backend.model.Match;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.user1.id = :userId OR m.user2.id = :userId")
    Page<Match> findByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<Match> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
}
