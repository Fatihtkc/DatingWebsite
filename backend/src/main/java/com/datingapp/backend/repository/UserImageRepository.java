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
import java.util.List;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    List<UserImage> findByUserId(Long userId);

    Page<UserImage> findByModerationStatus(ModerationStatus moderationStatus, Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM UserImage ui WHERE ui.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
