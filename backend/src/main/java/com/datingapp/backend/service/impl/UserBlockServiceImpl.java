package com.datingapp.backend.service.impl;

import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserBlock;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.repository.UserBlockRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.UserBlockService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor 
public class UserBlockServiceImpl implements UserBlockService {

    private final UserBlockRepository blockRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    @Override
    @Transactional
    public void blockUser(Long blockerId, Long blockedId){

        if (blockerId.equals(blockedId)) {
            throw new IllegalArgumentException("Kendinizi engelleyemezsiniz.");
        }

        if (blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId).isPresent()) {
            return;
        }

        User blocker = userRepository.findById(blockerId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + blockerId));
        User blocked = userRepository.findById(blockedId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + blockedId));

        UserBlock block = new UserBlock();
        block.setBlocker(blocker);
        block.setBlocked(blocked);
        block.setBlockedAt(LocalDateTime.now());
        blockRepository.save(block);

        matchRepository.findByUser1IdAndUser2Id(blockerId, blockedId)
            .ifPresent(matchRepository::delete);
        matchRepository.findByUser1IdAndUser2Id(blockedId, blockerId)
            .ifPresent(matchRepository::delete);
    }

    @Override
    @Transactional
    public void unblockUser(Long blockerId, Long blockedId){
        blockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Override
    public Page<User> getBlockedUsers(Long blockerId, Pageable pageable){

        return blockRepository.findByBlockerId(blockerId, pageable)
            .map(block -> block.getBlocked());
    }

    @Override
    public boolean isBlockedBetween(Long userId1, Long userId2){

        return blockRepository.existsBlockBetween(userId1, userId2);
    }
}