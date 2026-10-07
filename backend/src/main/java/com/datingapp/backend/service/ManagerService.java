package com.datingapp.backend.service;

import com.datingapp.backend.dto.PasswordChangeRequestDTO;
import com.datingapp.backend.dto.Manager.ManagerCreateDTO;
import com.datingapp.backend.dto.Manager.ManagerUpdateDTO;
import com.datingapp.backend.dto.Moderator.ModeratorCreateDTO;
import com.datingapp.backend.dto.Moderator.ModeratorUpdateDTO;
import com.datingapp.backend.dto.User.UserAdminDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ManagerService{

    ModeratorUpdateDTO hireModerator(ModeratorCreateDTO dto, MultipartFile file);
    void fireModerator(Long moderatorId);
    ModeratorUpdateDTO updateModerator(Long moderatorId, ModeratorUpdateDTO dto, MultipartFile file);
    ModeratorUpdateDTO getModeratorById(Long id);
    void updatePasswordModerator(Long authenticatedUserId,PasswordChangeRequestDTO request);
    Page<ModeratorUpdateDTO> listAllModerators(Pageable page);

    ManagerUpdateDTO hireManager(ManagerCreateDTO dto, MultipartFile file);
    void fireManager(Long managerId);
    ManagerUpdateDTO updateManager(Long managerId, ManagerUpdateDTO dto, MultipartFile file);
    void updatePasswordManager(Long authenticatedUserId, PasswordChangeRequestDTO request);
    Page<ManagerUpdateDTO> listAllManagers(Pageable page);
    ManagerUpdateDTO getManagerById(Long id);

    Page<UserAdminDTO> searchUsersByName(String name, Pageable page);
    UserAdminDTO updateUserInfo(Long userId, UserAdminDTO user);
  
}
