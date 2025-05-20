package com.datingapp.backend.service;

import com.datingapp.backend.dto.PasswordChangeRequest;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.User;
import java.util.List;

public interface AdminService {
    // Moderatör yönetimi
    Moderator hireModerator(Moderator moderator);
    void fireModerator(Long moderatorId);
    Moderator updateModerator(Long moderatorId, Moderator moderator);
    Moderator getModeratorById(Long id);
    boolean updatePassword(PasswordChangeRequest request);
    List<Moderator> listAllModerators();

    // Manager yönetimi
    Manager hireManager(Manager manager);
    void fireManager(Long managerId);
    Manager updateManager(Long managerId, Manager manager);
    boolean updatePasswordManager(PasswordChangeRequest request);
    List<Manager> listAllManagers();
    Manager getManagerById(Long id);

    // Kullanıcı arama ve güncelleme
    List<User> searchUsersByName(String name);
    User updateUserInfo(Long userId, User user);

    
}
