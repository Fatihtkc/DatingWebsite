package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import com.datingapp.backend.mapper.ComplaintMapper;
import com.datingapp.backend.mapper.UserMapper;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ComplaintRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.ModerationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final UserMapper userMapper;
    private final ComplaintMapper complaintMapper;

    @Override
    public UserAdminDTO approveUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApproved(true);
        user.setBanned(false);
        return userMapper.toAdminDTO(userRepository.save(user));
    }

    @Override
    public UserAdminDTO banUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        return userMapper.toAdminDTO(userRepository.save(user));
    }

    @Override
    public List<UserAdminDTO> listPendingUsers() {
        return userRepository.findByApprovedFalse().stream()
            .map(userMapper::toAdminDTO)
            .toList();
    }

    @Override
    public List<ComplaintDTO> listAllComplaints() {
        return complaintRepository.findAll().stream()
            .map(complaintMapper::toDTO)
            .toList();
    }

    @Override
    public ComplaintDTO getComplaint(Long complaintId) {
        return complaintMapper.toDTO(complaintRepository.findById(complaintId)
            .orElseThrow(() -> new RuntimeException("Complaint not found")));
    }

    @Override
    public void deleteComplaint(Long complaintId) {
        complaintRepository.deleteById(complaintId);
    }
}
