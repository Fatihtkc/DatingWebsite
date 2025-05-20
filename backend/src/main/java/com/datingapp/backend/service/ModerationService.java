package com.datingapp.backend.service;

import com.datingapp.backend.model.Complaint;
import com.datingapp.backend.model.User;
import java.util.List;

public interface ModerationService {
    // 1. Yeni kullanıcı profillerini onayla
    User approveUser(Long userId);

    // 2. Kullanıcıyı banla
    User banUser(Long userId);

    // 3. Bekleyen profilleri listele (onay bekleyenler)
    List<User> listPendingUsers();

    // 4. Tüm şikayetleri listele
    List<Complaint> listAllComplaints();

    // 5. Belirli bir şikayeti detaylı getir
    Complaint getComplaint(Long complaintId);

    // 6. Şikayeti sil
    void deleteComplaint(Long complaintId);
}
