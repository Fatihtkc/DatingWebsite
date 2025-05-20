package com.datingapp.backend.repository;

import com.datingapp.backend.model.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    // Belirli bir kullanıcının yaptığı beğenileri getir
    List<UserLike> findByLikerId(Long likerId);

    // Belirli bir kullanıcının aldığı beğenileri getir
    List<UserLike> findByLikedId(Long likedId);

    // İki kullanıcı arasındaki beğeni kaydının var olup olmadığını kontrol et
    Optional<UserLike> findByLikerIdAndLikedId(Long likerId, Long likedId);
}
