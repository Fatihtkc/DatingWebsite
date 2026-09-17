package com.datingapp.backend.service;

import com.datingapp.backend.dto.ManagerDTO;
import com.datingapp.backend.dto.ModeratorDTO;
import com.datingapp.backend.dto.PasswordChangeRequest;
import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.User;
import java.util.List;

public interface ManagerService {
    // Moderatör yönetimi
    ModeratorDTO hireModerator(Moderator moderator);
    void fireModerator(Long moderatorId);
    ModeratorDTO updateModerator(Long moderatorId, Moderator moderator);
    ModeratorDTO getModeratorById(Long id);
    boolean updatePassword(PasswordChangeRequest request);
    List<ModeratorDTO> listAllModerators();

    // Manager yönetimi
    ManagerDTO hireManager(Manager manager);
    void fireManager(Long managerId);
    ManagerDTO updateManager(Long managerId, Manager manager);
    boolean updatePasswordManager(PasswordChangeRequest request);
    List<ManagerDTO> listAllManagers();
    ManagerDTO getManagerById(Long id);

    // Kullanıcı arama ve güncelleme
    List<UserAdminDTO> searchUsersByName(String name);
    UserAdminDTO updateUserInfo(Long userId, User user);

    
}
