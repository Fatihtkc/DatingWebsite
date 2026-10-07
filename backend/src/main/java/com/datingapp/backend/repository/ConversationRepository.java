package com.datingapp.backend.repository;

import com.datingapp.backend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUser1IdAndUser2Id(Long user1Id,Long user2Id);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Conversation c WHERE c.id = :conversationId AND (c.user1.id = :userId OR c.user2.id = :userId)")
    boolean existsByIdAndUserId(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}