package com.datingapp.backend.service;

import com.datingapp.backend.model.Complaint;
import java.util.List;

public interface ComplaintService {
    List<Complaint> getAllComplaints();
    Complaint getComplaint(Long id);
    Complaint createComplaint(Complaint complaint);
    void deleteComplaint(Long id);
}
