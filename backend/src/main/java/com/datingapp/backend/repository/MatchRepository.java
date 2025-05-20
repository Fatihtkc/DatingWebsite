package com.datingapp.backend.repository;

import com.datingapp.backend.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.user1.id = :userId1 OR m.user2.id = :userId2")
    List<Match> findByUser1IdOrUser2Id(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // İki kullanıcı arasında match olup olmadığını kontrol et (alternatif sorgu)
    List<Match> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
}
