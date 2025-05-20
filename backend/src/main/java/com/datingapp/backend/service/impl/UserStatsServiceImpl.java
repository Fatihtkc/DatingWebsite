package com.datingapp.backend.service.impl;

import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class UserStatsServiceImpl implements UserStatsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.countAllUsers());
        stats.put("averageAge", userRepository.averageAge());

        Map<String, Long> genderDist = new HashMap<>();
        for (Object[] row : userRepository.genderDistribution()) {
            String key = row[0] != null ? (String) row[0] : "Unknown";
            genderDist.put(key, (Long) row[1]);
        }

        stats.put("genderDistribution", genderDist);

        Map<String, Long> relationshipDist = new HashMap<>();
        for (Object[] row : userRepository.relationshipTypes()) {
            String key = row[0] != null ? (String) row[0] : "Unknown";
            relationshipDist.put(key, (Long) row[1]);
        }
        stats.put("relationshipTypes", relationshipDist);

        List<Object[]> avgHWList = userRepository.averageHeightWeight();
        if (avgHWList != null && !avgHWList.isEmpty()) {
            Object[] avgHW = avgHWList.get(0);
            stats.put("averageHeight", avgHW[0]);
            stats.put("averageWeight", avgHW[1]);
        } else {
            stats.put("averageHeight", null);
            stats.put("averageWeight", null);
        }
        

        return stats;
    }
}
