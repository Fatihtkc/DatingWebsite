package com.datingapp.backend.service;

import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ModerationService {
    UserAdminDTO approveUser(Long userId);

    UserAdminDTO banUser(Long userId);

    Page<UserAdminDTO> listPendingUsers(Pageable pageable);

    Page<ComplaintDTO> listAllComplaints(Pageable pageable);

    ComplaintDTO getComplaint(Long complaintId);

    void deleteComplaint(Long complaintId);
}
