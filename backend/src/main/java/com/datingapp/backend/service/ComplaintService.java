package com.datingapp.backend.service;

import com.datingapp.backend.dto.Complaint.ComplaintCreateDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;

import java.util.List;

public interface ComplaintService {
    List<ComplaintDTO> getAllComplaints();
    ComplaintDTO getComplaint(Long id);
    ComplaintDTO createComplaint(Long complainantId, ComplaintCreateDTO dto);
    void deleteComplaint(Long id);
}
