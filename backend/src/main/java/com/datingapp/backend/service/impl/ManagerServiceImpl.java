package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.ManagerDTO;
import com.datingapp.backend.dto.ModeratorDTO;
import com.datingapp.backend.dto.PasswordChangeRequest;
import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.exception.InvalidPasswordException;
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
import com.datingapp.backend.service.FileStorageService;
import com.datingapp.backend.service.FileUploadResult;
import com.datingapp.backend.service.ManagerService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

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
    private final FileStorageService storage;

    @Override
    public ModeratorDTO hireModerator(Moderator moderator, MultipartFile file){

        Optional<Moderator> byEmail = moderatorRepo.findByEmail(moderator.getEmail());
        if (byEmail.isPresent()) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        Optional<Moderator> byPhone = moderatorRepo.findByPhone(moderator.getPhone());
        if (byPhone.isPresent()) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        moderator.setPassword(passwordEncoder.encode(moderator.getPassword()));

        if (file != null && !file.isEmpty()) {
            FileUploadResult result = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(result.url()).toUriString();
            moderator.setImageUrl(publicUrl);
        }

        return moderatorMapper.toDTO(moderatorRepo.save(moderator));
    }

    @Override
    public void fireModerator(Long moderatorId){
        moderatorRepo.deleteById(moderatorId);
    }

    @Override
    public ModeratorDTO updateModerator(Long moderatorId, Moderator moderator, MultipartFile file){
        Moderator existing = moderatorRepo.findById(moderatorId)
            .orElseThrow(() -> new RuntimeException("Moderator not found"));

    
        Optional<Moderator> byEmail = moderatorRepo.findByEmail(moderator.getEmail());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(moderatorId)) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        Optional<Moderator> byPhone = moderatorRepo.findByPhone(moderator.getPhone());
        if (byPhone.isPresent() && !byPhone.get().getId().equals(moderatorId)){
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }
        existing.setFirstName(moderator.getFirstName());
        existing.setLastName(moderator.getLastName());
        existing.setEmail(moderator.getEmail());
        existing.setPhone(moderator.getPhone());

        if (file != null && !file.isEmpty()) {
            FileUploadResult result = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(result.url()).toUriString();
            moderator.setImageUrl(publicUrl);
        }
        if (moderator.getImageUrl() != null) {
            existing.setImageUrl(moderator.getImageUrl());
        }

        return moderatorMapper.toDTO(moderatorRepo.save(existing));
    }

    @Override
    public Page<ModeratorDTO> listAllModerators(Pageable pageable){
        return moderatorRepo.findAll(pageable).map(moderatorMapper::toDTO);
    }

    @Override
    public ModeratorDTO getModeratorById(Long id){
        return moderatorMapper.toDTO(moderatorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Moderator not found with id: " + id)));
    }

    @Override
    public boolean updatePassword(PasswordChangeRequest request){

        Optional<Moderator> optionalModerator = moderatorRepo.findById(request.getId());
        if (optionalModerator.isEmpty()) {
            return false;
        }

        Moderator moderator = optionalModerator.get();

        if (!passwordEncoder.matches(request.getOldPassword(), moderator.getPassword())){
            throw new InvalidPasswordException("Old password is incorrect.");
        }

        String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
        moderator.setPassword(hashedNewPassword);
        moderatorRepo.save(moderator);
        return true;
    }

    @Override
    public boolean updatePasswordManager(PasswordChangeRequest request){

        Optional<Manager> optionalManager = managerRepo.findById(request.getId());
        if (optionalManager.isEmpty()) {
            return false;
        }

        Manager manager = optionalManager.get();

        if (!passwordEncoder.matches(request.getOldPassword(), manager.getPassword())){
            return false;
        }

        String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
        manager.setPassword(hashedNewPassword);
        managerRepo.save(manager);
        return true;
    }

    @Override
    public ManagerDTO hireManager(Manager manager, MultipartFile file){

        Optional<Manager> byEmail = managerRepo.findByEmail(manager.getEmail());
        if (byEmail.isPresent()) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        Optional<Manager> byPhone = managerRepo.findByPhone(manager.getPhone());
        if (byPhone.isPresent()) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        manager.setPassword(passwordEncoder.encode(manager.getPassword()));

        if (file != null && !file.isEmpty()) {
            FileUploadResult result = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(result.url()).toUriString();
            manager.setImageUrl(publicUrl);
        }

        return managerMapper.toDTO(managerRepo.save(manager));
    }

    @Override
    public void fireManager(Long managerId){
        managerRepo.deleteById(managerId);
    }

    @Override
    public ManagerDTO updateManager(Long managerId, Manager manager, MultipartFile file){

        Manager existing = managerRepo.findById(managerId).orElseThrow(() -> new RuntimeException("Manager not found"));

        Optional<Manager> byEmail = managerRepo.findByEmail(manager.getEmail());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(managerId)) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        Optional<Manager> byPhone = managerRepo.findByPhone(manager.getPhone());
        if (byPhone.isPresent() && !byPhone.get().getId().equals(managerId)) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        existing.setFirstName(manager.getFirstName());
        existing.setLastName(manager.getLastName());
        existing.setEmail(manager.getEmail());
        existing.setPhone(manager.getPhone());
        existing.setRole(manager.getRole());
        if (file != null && !file.isEmpty()) {
            FileUploadResult result = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(result.url()).toUriString();
            manager.setImageUrl(publicUrl);
        }
        if (manager.getImageUrl() != null) {
            existing.setImageUrl(manager.getImageUrl());
        }

        return managerMapper.toDTO(managerRepo.save(existing));
    }

    @Override
    public Page<ManagerDTO> listAllManagers(Pageable pageable){
        return managerRepo.findAll(pageable).map(managerMapper::toDTO);
    }

    @Override
    public ManagerDTO getManagerById(Long id){
        return managerMapper.toDTO(managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id)));
    }

    @Override
    public Page<UserAdminDTO> searchUsersByName(String name, Pageable pageable){
        return userRepo.searchByFullName(name, pageable).map(userMapper::toAdminDTO);
    }

    @Override
    public UserAdminDTO updateUserInfo(Long userId, User user){

        User existing = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setLocation(user.getLocation());
        existing.setShorterBio(user.getShorterBio());
        return userMapper.toAdminDTO(userRepo.save(existing));

    }
}
