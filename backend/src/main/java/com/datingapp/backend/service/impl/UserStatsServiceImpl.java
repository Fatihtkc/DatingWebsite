package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Stats.UserStatsDTO;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.UserStatsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {

    private final UserRepository userRepository;

    @Override
    public UserStatsDTO getStats() {

        return new UserStatsDTO(
            userRepository.countAllUsers(),
            userRepository.averageAge(),
            userRepository.genderDistribution(),
            userRepository.relationshipTypes(),
            userRepository.averageHeightWeight()
        );
    }
}
