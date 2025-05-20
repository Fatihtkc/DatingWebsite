package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Complaint;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ComplaintRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.ModerationService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ModerationServiceImpl implements ModerationService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;

    public ModerationServiceImpl(UserRepository userRepository,
                                 ComplaintRepository complaintRepository) {
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
    }

    @Override
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApproved(true);       // Entity’de bir `approved` boolean alanı olmalı
        user.setBanned(false);
        return userRepository.save(user);
    }

    @Override
    public User banUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        return userRepository.save(user);
    }

    @Override
    public List<User> listPendingUsers() {
        return userRepository.findByApprovedFalse();  // repository’de bu metod tanımlı olmalı
    }

    @Override
    public List<Complaint> listAllComplaints() {
        return complaintRepository.findAll();
    }

    @Override
    public Complaint getComplaint(Long complaintId) {
        return complaintRepository.findById(complaintId)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    @Override
    public void deleteComplaint(Long complaintId) {
        complaintRepository.deleteById(complaintId);
    }
}
