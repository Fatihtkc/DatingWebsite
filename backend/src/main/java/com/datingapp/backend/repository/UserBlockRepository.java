package com.datingapp.backend.repository;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserBlock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    Optional<UserBlock> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    Page<UserBlock> findByBlockerId(Long blockerId, Pageable pageable);

    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
        FROM UserBlock b
        WHERE (b.blocker.id = :userId1 AND b.blocked.id = :userId2)
           OR (b.blocker.id = :userId2 AND b.blocked.id = :userId1)
        """)
    boolean existsBlockBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query(
        value = "SELECT u FROM UserBlock b JOIN b.blocked u WHERE b.blocker.id = :blockerId",
        countQuery = "SELECT COUNT(b) FROM UserBlock b WHERE b.blocker.id = :blockerId")
    Page<User> findBlockedUsers(@Param("blockerId") Long blockerId, Pageable pageable);

    void deleteByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}