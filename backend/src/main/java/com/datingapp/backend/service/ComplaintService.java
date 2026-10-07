package com.datingapp.backend.service;

import com.datingapp.backend.dto.Complaint.ComplaintCreateDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ComplaintService {
    Page<ComplaintDTO> getAllComplaints(Pageable pageable);
    ComplaintDTO getComplaint(Long id);
    ComplaintDTO createComplaint(Long complainantId, ComplaintCreateDTO dto, List<MultipartFile> files);
    void deleteComplaint(Long id);
}
