package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Complaint;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ComplaintRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.ComplaintService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }
    
    @Override
    public Complaint getComplaint(Long id) {
        return complaintRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    @Override
    public Complaint createComplaint(Complaint complaint) {
        // complainant ve complained kullanıcılarını veritabanından çek
        User complainant = userRepository.findById(complaint.getComplainant().getId())
                .orElseThrow(() -> new RuntimeException("Complainant not found"));

        User complained = userRepository.findById(complaint.getComplained().getId())
                .orElseThrow(() -> new RuntimeException("Complained user not found"));

        complaint.setComplainant(complainant);
        complaint.setComplained(complained);

        complaint.setComplaintDate(LocalDateTime.now()); // şikayet tarihi şuanın zamanı olsun

        return complaintRepository.save(complaint);
    }
    

    @Override
    public void deleteComplaint(Long id) {
        if (!complaintRepository.existsById(id)) {
            throw new RuntimeException("Complaint not found with id: " + id);
        }
        complaintRepository.deleteById(id);
    }
}
