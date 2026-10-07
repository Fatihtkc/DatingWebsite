package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Complaint.ComplaintCreateDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import com.datingapp.backend.exception.ResourceNotFoundException;
import com.datingapp.backend.mapper.ComplaintMapper;
import com.datingapp.backend.model.Complaint;
import com.datingapp.backend.model.ComplaintImage;
import com.datingapp.backend.model.User;
import com.datingapp.backend.projection.ComplaintProjection;
import com.datingapp.backend.repository.ComplaintRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.ComplaintService;
import com.datingapp.backend.service.FileUploadResult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService{

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final ComplaintMapper complaintMapper;
    private final FileStorageServiceImpl storage;
    
    @Override
    public Page<ComplaintDTO> getAllComplaints(Pageable pageable){

        return complaintRepository.findAllComplaintProjections(pageable).map(complaintMapper::toDTO);
    }
    
    @Override
    public ComplaintDTO getComplaint(Long id){

        ComplaintProjection projection = complaintRepository.findComplaintProjectionById(id);

        if (projection == null){
            throw new ResourceNotFoundException("Complaint not found with id: " + id);
        }

        return complaintMapper.toDTO(projection);
    }

    @Override
    @Transactional 
    public ComplaintDTO createComplaint(Long complainantId, ComplaintCreateDTO dto, List<MultipartFile> files){
        
        User complainant = userRepository.findById(complainantId).orElseThrow(() -> new ResourceNotFoundException("Complainant not found"));

        User complained = userRepository.findById(dto.getComplainedId()).orElseThrow(() -> new ResourceNotFoundException("Complained user not found"));

        Complaint complaint = new Complaint();
        complaint.setComplainant(complainant);
        complaint.setComplained(complained);
        complaint.setReason(dto.getReason());
        complaint.setComplaintDate(LocalDateTime.now());

        if (files != null && !files.isEmpty()){

            for (MultipartFile file : files) {

                FileUploadResult result = storage.storeFile(file);
                ComplaintImage image = new ComplaintImage();
                image.setImageUrl(result.url());
                complaint.addImage(image);
            }
        }

        Complaint savedComplaint = complaintRepository.save(complaint);
        ComplaintProjection projection = complaintRepository.findComplaintProjectionById(savedComplaint.getId());

        return complaintMapper.toDTO(projection);
    }
    

    @Override
    @Transactional 
    public void deleteComplaint(Long id){

        if (!complaintRepository.existsById(id)) {
            throw new ResourceNotFoundException("Complaint not found with id: " + id);
        }
        complaintRepository.deleteById(id);
    }
}
