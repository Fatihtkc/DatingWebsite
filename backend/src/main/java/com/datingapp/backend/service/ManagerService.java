package com.datingapp.backend.service;

import com.datingapp.backend.dto.ManagerDTO;
import com.datingapp.backend.dto.ModeratorDTO;
import com.datingapp.backend.dto.PasswordChangeRequest;
import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ManagerService {
    ModeratorDTO hireModerator(Moderator moderator, MultipartFile file);
    void fireModerator(Long moderatorId);
    ModeratorDTO updateModerator(Long moderatorId, Moderator moderator, MultipartFile file);
    ModeratorDTO getModeratorById(Long id);
    boolean updatePassword(PasswordChangeRequest request);
    Page<ModeratorDTO> listAllModerators(Pageable page);

    ManagerDTO hireManager(Manager manager, MultipartFile file);
    void fireManager(Long managerId);
    ManagerDTO updateManager(Long managerId, Manager manager, MultipartFile file);
    boolean updatePasswordManager(PasswordChangeRequest request);
    Page<ManagerDTO> listAllManagers(Pageable page);
    ManagerDTO getManagerById(Long id);

    Page<UserAdminDTO> searchUsersByName(String name, Pageable page);
    UserAdminDTO updateUserInfo(Long userId, User user);

    
}
