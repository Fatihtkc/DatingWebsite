package com.datingapp.backend.repository;

import com.datingapp.backend.enums.ModerationStatus;
import com.datingapp.backend.model.UserImage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    List<UserImage> findByUserIdAndModerationStatus(Long userId, ModerationStatus moderationStatus);

    Optional<UserImage> findByIdAndModerationStatus(Long imageId, ModerationStatus moderationStatus);

    List<UserImage> findByUserId(Long userId);

    Page<UserImage> findByModerationStatus(ModerationStatus moderationStatus, Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM UserImage ui WHERE ui.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM UserImage i WHERE i.user.id IN :userIds AND i.moderationStatus = :status")
    List<UserImage> findImagesByUserIdsAndStatus(@Param("userIds") Collection<Long> userIds, @Param("status") ModerationStatus status);
}
