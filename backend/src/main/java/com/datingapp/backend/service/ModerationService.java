package com.datingapp.backend.service;

import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import java.util.List;

public interface ModerationService {
    // 1. Yeni kullanıcı profillerini onayla
    UserAdminDTO approveUser(Long userId);

    // 2. Kullanıcıyı banla
    UserAdminDTO banUser(Long userId);

    // 3. Bekleyen profilleri listele (onay bekleyenler)
    List<UserAdminDTO> listPendingUsers();

    // 4. Tüm şikayetleri listele
    List<ComplaintDTO> listAllComplaints();

    // 5. Belirli bir şikayeti detaylı getir
    ComplaintDTO getComplaint(Long complaintId);

    // 6. Şikayeti sil
    void deleteComplaint(Long complaintId);
}
