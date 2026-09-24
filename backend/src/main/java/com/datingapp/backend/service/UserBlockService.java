package com.datingapp.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.datingapp.backend.model.User;

public interface UserBlockService {

    void blockUser(Long blockerId, Long blockedId);
    void unblockUser(Long blockerId, Long blockedId);
    Page<User> getBlockedUsers(Long blockerId, Pageable pageable);
    boolean isBlockedBetween(Long userId1, Long userId2);
}
