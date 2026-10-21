package com.datingapp.backend.repository;

import com.datingapp.backend.model.UserLike;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    Page<UserLike> findByLikerId(Long likerId, Pageable pageable);

    Optional<UserLike> findByLikerIdAndLikedId(Long likerId, Long likedId);

    Optional<UserLike> findByIdAndLikerId(Long id, Long likerId);
}
