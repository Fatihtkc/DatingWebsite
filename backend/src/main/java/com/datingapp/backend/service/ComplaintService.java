package com.datingapp.backend.service;

import com.datingapp.backend.dto.Complaint.ComplaintCreateDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComplaintService {
    Page<ComplaintDTO> getAllComplaints(Pageable pageable);
    ComplaintDTO getComplaint(Long id);
    ComplaintDTO createComplaint(Long complainantId, ComplaintCreateDTO dto);
    void deleteComplaint(Long id);
}
