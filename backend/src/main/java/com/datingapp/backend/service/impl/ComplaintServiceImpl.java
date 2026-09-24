package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Complaint.ComplaintCreateDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import com.datingapp.backend.mapper.ComplaintMapper;
import com.datingapp.backend.mapper.ImageMapper;
import com.datingapp.backend.model.Complaint;
import com.datingapp.backend.model.ComplaintImage;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ComplaintRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.ComplaintService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService{

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final ComplaintMapper complaintMapper;
    private final ImageMapper imageMapper;
    
    @Override
    public Page<ComplaintDTO> getAllComplaints(Pageable pageable){

        return complaintRepository.findAll(pageable).map(complaintMapper::toDTO);
    }
    
    @Override
    public ComplaintDTO getComplaint(Long id){

        return complaintRepository.findById(id).map(complaintMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    @Override
    public ComplaintDTO createComplaint(Long complainantId, ComplaintCreateDTO dto){
        
        User complainant = userRepository.findById(complainantId)
                .orElseThrow(() -> new RuntimeException("Complainant not found"));

        User complained = userRepository.findById(dto.getComplainedId())
                .orElseThrow(() -> new RuntimeException("Complained user not found"));

        Complaint complaint = new Complaint();
        complaint.setComplainant(complainant);
        complaint.setComplained(complained);
        complaint.setReason(dto.getReason());
        complaint.setComplaintDate(LocalDateTime.now());

        if (dto.getImages() != null){

            List<ComplaintImage> images = dto.getImages()
                    .stream()
                    .map(imageMapper::toComplaintImageEntity)
                    .toList();

            for (ComplaintImage image : images){
                complaint.addImage(image);
            }
        }

        return complaintMapper.toDTO(complaintRepository.save(complaint));
    }
    

    @Override
    public void deleteComplaint(Long id){

        if (!complaintRepository.existsById(id)) {
            throw new RuntimeException("Complaint not found with id: " + id);
        }
        complaintRepository.deleteById(id);
    }
}
