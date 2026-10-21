package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.mapper.UserMapper;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserBlock;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.repository.UserBlockRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.UserBlockService;
import com.datingapp.backend.service.UserImageService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor 
public class UserBlockServiceImpl implements UserBlockService {

    private final UserBlockRepository blockRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final UserImageService userImageService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void blockUser(Long blockerId, Long blockedId){

        if (blockerId.equals(blockedId)){
            throw new IllegalArgumentException("You cannot block yourself.");
        }

        if (blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId).isPresent()){
            return;
        }

        User blocker = userRepository.findById(blockerId).orElseThrow(() -> new UserNotFoundException("User not found: " + blockerId));
        User blocked = userRepository.findById(blockedId).orElseThrow(() -> new UserNotFoundException("User not found: " + blockedId));

        UserBlock block = new UserBlock();
        block.setBlocker(blocker);
        block.setBlocked(blocked);
        block.setBlockedAt(LocalDateTime.now());
        blockRepository.save(block);

        matchRepository.findByUser1IdAndUser2Id(blockerId, blockedId).ifPresent(matchRepository::delete);
        matchRepository.findByUser1IdAndUser2Id(blockedId, blockerId).ifPresent(matchRepository::delete);
    }

    @Override
    @Transactional
    public void unblockUser(Long blockerId, Long blockedId){
        blockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> getBlockedUsers(Long blockerId, Pageable pageable){

        Page<User> users = blockRepository.findBlockedUsers(blockerId, pageable);

        Map<Long, List<UserImage>> imagesByUserId = userImageService.loadApprovedImages(users.getContent());

        return users.map(user -> userMapper.toDTO(user, imagesByUserId.getOrDefault(user.getId(), List.of())));
    }

    @Override
    public boolean isBlockedBetween(Long userId1, Long userId2){

        return blockRepository.existsBlockBetween(userId1, userId2);
    }
}