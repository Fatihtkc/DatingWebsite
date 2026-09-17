package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.ManagerDTO;
import com.datingapp.backend.dto.ModeratorDTO;
import com.datingapp.backend.dto.PasswordChangeRequest;
import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.exception.UniqueConstraintViolationException;
import com.datingapp.backend.mapper.ManagerMapper;
import com.datingapp.backend.mapper.ModeratorMapper;
import com.datingapp.backend.mapper.UserMapper;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ManagerRepository;
import com.datingapp.backend.repository.ModeratorRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.ManagerService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ModeratorRepository moderatorRepo;
    private final ManagerRepository managerRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModeratorMapper moderatorMapper;
    private final ManagerMapper managerMapper;
    private final UserMapper userMapper;

    @Override
    public ModeratorDTO hireModerator(Moderator moderator) {

        Optional<Moderator> byEmail = moderatorRepo.findByEmail(moderator.getEmail());
        if (byEmail.isPresent()) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        // Phone kontrolü
        Optional<Moderator> byPhone = moderatorRepo.findByPhone(moderator.getPhone());
        if (byPhone.isPresent()) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        moderator.setPassword(passwordEncoder.encode(moderator.getPassword()));

        return moderatorMapper.toDTO(moderatorRepo.save(moderator));
    }

    @Override
    public void fireModerator(Long moderatorId) {
        moderatorRepo.deleteById(moderatorId);
    }

    @Override
    public ModeratorDTO updateModerator(Long moderatorId, Moderator moderator) {
        Moderator existing = moderatorRepo.findById(moderatorId)
            .orElseThrow(() -> new RuntimeException("Moderator not found"));

    
        // Email kontrolü
        Optional<Moderator> byEmail = moderatorRepo.findByEmail(moderator.getEmail());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(moderatorId)) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        // Phone kontrolü
        Optional<Moderator> byPhone = moderatorRepo.findByPhone(moderator.getPhone());
        if (byPhone.isPresent() && !byPhone.get().getId().equals(moderatorId)) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }
        existing.setFirstName(moderator.getFirstName());
        existing.setLastName(moderator.getLastName());
        existing.setEmail(moderator.getEmail());
        existing.setPhone(moderator.getPhone());
        if (moderator.getImageUrl() != null) {
            existing.setImageUrl(moderator.getImageUrl());
        }
        return moderatorMapper.toDTO(moderatorRepo.save(existing));
    }

    @Override
    public List<ModeratorDTO> listAllModerators() {
        return moderatorRepo.findAll().stream().map(moderatorMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ModeratorDTO getModeratorById(Long id) {
        return moderatorMapper.toDTO(moderatorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Moderator not found with id: " + id)));
    }

    @Override
    public boolean updatePassword(PasswordChangeRequest request) {

        Optional<Moderator> optionalModerator = moderatorRepo.findById(request.getId());
        if (optionalModerator.isEmpty()) {
            return false;
        }

        Moderator moderator = optionalModerator.get();

        if (!passwordEncoder.matches(request.getOldPassword(), moderator.getPassword())) {
            return false;
        }

        String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
        moderator.setPassword(hashedNewPassword);
        moderatorRepo.save(moderator);
        return true;
    }

    @Override
    public boolean updatePasswordManager(PasswordChangeRequest request) {

        Optional<Manager> optionalManager = managerRepo.findById(request.getId());
        if (optionalManager.isEmpty()) {
            return false;
        }

        Manager manager = optionalManager.get();

        if (!passwordEncoder.matches(request.getOldPassword(), manager.getPassword())) {
            return false;
        }

        String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
        manager.setPassword(hashedNewPassword);
        managerRepo.save(manager);
        return true;
    }

    @Override
    public ManagerDTO hireManager(Manager manager) {

        Optional<Manager> byEmail = managerRepo.findByEmail(manager.getEmail());
        if (byEmail.isPresent()) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        // Phone kontrolü
        Optional<Manager> byPhone = managerRepo.findByPhone(manager.getPhone());
        if (byPhone.isPresent()) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        manager.setPassword(passwordEncoder.encode(manager.getPassword()));


        return managerMapper.toDTO(managerRepo.save(manager));
    }

    @Override
    public void fireManager(Long managerId) {
        managerRepo.deleteById(managerId);
    }

    @Override
    public ManagerDTO updateManager(Long managerId, Manager manager) {
        Manager existing = managerRepo.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

                    // Email kontrolü
        Optional<Manager> byEmail = managerRepo.findByEmail(manager.getEmail());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(managerId)) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        // Phone kontrolü
        Optional<Manager> byPhone = managerRepo.findByPhone(manager.getPhone());
        if (byPhone.isPresent() && !byPhone.get().getId().equals(managerId)) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        existing.setFirstName(manager.getFirstName());
        existing.setLastName(manager.getLastName());
        existing.setEmail(manager.getEmail());
        existing.setPhone(manager.getPhone());
        existing.setRole(manager.getRole());
        if (manager.getImageUrl() != null) {
            existing.setImageUrl(manager.getImageUrl());
        }
        return managerMapper.toDTO(managerRepo.save(existing));
    }

    @Override
    public List<ManagerDTO> listAllManagers() {
        return managerRepo.findAll().stream().map(managerMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ManagerDTO getManagerById(Long id) {
        return managerMapper.toDTO(managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id)));
    }

    @Override
    public List<UserAdminDTO> searchUsersByName(String name) {
        return userRepo.searchByFullName(name).stream().map(userMapper::toAdminDTO).collect(Collectors.toList());
    }

    @Override
    public UserAdminDTO updateUserInfo(Long userId, User user) {
        User existing = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setLocation(user.getLocation());
        existing.setShorterBio(user.getShorterBio());
        return userMapper.toAdminDTO(userRepo.save(existing));
    }
}
