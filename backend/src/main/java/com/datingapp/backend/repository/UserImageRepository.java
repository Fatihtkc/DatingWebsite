package com.datingapp.backend.repository;

import com.datingapp.backend.model.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    // Belirli bir kullanıcıya ait tüm resimleri getir
    List<UserImage> findByUserId(Long userId);
    
    @Modifying
    @Query("DELETE FROM UserImage ui WHERE ui.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
