package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.PasswordChangeRequestDTO;
import com.datingapp.backend.dto.Manager.ManagerCreateDTO;
import com.datingapp.backend.dto.Manager.ManagerUpdateDTO;
import com.datingapp.backend.dto.Moderator.ModeratorCreateDTO;
import com.datingapp.backend.dto.Moderator.ModeratorUpdateDTO;
import com.datingapp.backend.dto.User.UserAdminDTO;
import com.datingapp.backend.enums.Role;
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
import org.springframework.transaction.annotation.Transactional;
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
    public ModeratorUpdateDTO hireModerator(ModeratorCreateDTO dto, MultipartFile file){

        if (moderatorRepo.findByEmail(dto.getEmail()).isPresent()){
            throw new UniqueConstraintViolationException("Email is already in use");
        }

        if (moderatorRepo.findByPhone(dto.getPhone()).isPresent()){
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        Moderator moderator = moderatorMapper.toEntity(dto);

        moderator.setRole(Role.MODERATOR);
        moderator.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (file != null && !file.isEmpty()){

            FileUploadResult result = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(result.url()).toUriString();

            moderator.setImageUrl(publicUrl);
        }

        Moderator saved = moderatorRepo.save(moderator);

        return moderatorMapper.toDTO(saved);
    }

    @Override
    public void fireModerator(Long moderatorId){
        moderatorRepo.deleteById(moderatorId);
    }

    @Override
    public ModeratorUpdateDTO updateModerator(Long moderatorId, ModeratorUpdateDTO dto, MultipartFile file){

        Moderator existing = moderatorRepo.findById(moderatorId).orElseThrow(() -> new RuntimeException("Moderator not found"));

        Optional<Moderator> byEmail = moderatorRepo.findByEmail(dto.getEmail());

        if (byEmail.isPresent() && !byEmail.get().getId().equals(moderatorId)){
            throw new UniqueConstraintViolationException("Email is already in use");
        }

        Optional<Moderator> byPhone = moderatorRepo.findByPhone(dto.getPhone());

        if (byPhone.isPresent() && !byPhone.get().getId().equals(moderatorId)){
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        moderatorMapper.updateEntity(dto, existing);

        if (file != null && !file.isEmpty()){

            FileUploadResult result = storage.storeFile(file);

            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(result.url()).toUriString();

            existing.setImageUrl(publicUrl);
        }

        Moderator saved = moderatorRepo.save(existing);

        return moderatorMapper.toDTO(saved);

    }

    @Override
    public Page<ModeratorUpdateDTO> listAllModerators(Pageable pageable){
        return moderatorRepo.findAll(pageable).map(moderatorMapper::toDTO);
    }

    @Override
    public ModeratorUpdateDTO getModeratorById(Long id){
        return moderatorMapper.toDTO(moderatorRepo.findById(id).orElseThrow(() -> new RuntimeException("Moderator not found with id: " + id)));
    }

    @Override
    public boolean updatePasswordModerator(Long authenticatedUserId,PasswordChangeRequestDTO request){

        Optional<Moderator> optionalModerator = moderatorRepo.findById(authenticatedUserId);
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
    public boolean updatePasswordManager(Long authenticatedUserId,PasswordChangeRequestDTO request){

        Optional<Manager> optionalManager = managerRepo.findById(authenticatedUserId);
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
    public ManagerUpdateDTO hireManager(ManagerCreateDTO dto, MultipartFile file){

        if (managerRepo.findByEmail(dto.getEmail()).isPresent()){
            throw new UniqueConstraintViolationException("Email is already in use");
        }

        if (managerRepo.findByPhone(dto.getPhone()).isPresent()){
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        Manager manager = managerMapper.toEntity(dto);

        manager.setRole(Role.MANAGER);
        manager.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (file != null && !file.isEmpty()){

            FileUploadResult result = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(result.url()).toUriString();

            manager.setImageUrl(publicUrl);
        }

        Manager saved = managerRepo.save(manager);

        return managerMapper.toDTO(saved);
    }

    @Override
    public void fireManager(Long managerId){
        managerRepo.deleteById(managerId);
    }

    @Override
    public ManagerUpdateDTO updateManager(Long managerId, ManagerUpdateDTO dto, MultipartFile file){

        Manager existing = managerRepo.findById(managerId).orElseThrow(() -> new RuntimeException("Moderator not found"));

        Optional<Manager> byEmail = managerRepo.findByEmail(dto.getEmail());

        if (byEmail.isPresent() && !byEmail.get().getId().equals(managerId)){
            throw new UniqueConstraintViolationException("Email is already in use");
        }

        Optional<Manager> byPhone = managerRepo.findByPhone(dto.getPhone());

        if (byPhone.isPresent() && !byPhone.get().getId().equals(managerId)){
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        managerMapper.updateEntity(dto, existing);

        if (file != null && !file.isEmpty()){

            FileUploadResult result = storage.storeFile(file);

            String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(result.url()).toUriString();

            existing.setImageUrl(publicUrl);
        }

        Manager saved = managerRepo.save(existing);

        return managerMapper.toDTO(saved);
    }

    @Override
    public Page<ManagerUpdateDTO> listAllManagers(Pageable pageable){
        return managerRepo.findAll(pageable).map(managerMapper::toDTO);
    }

    @Override
    public ManagerUpdateDTO getManagerById(Long id){
        return managerMapper.toDTO(managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id)));
    }

    @Override
    public Page<UserAdminDTO> searchUsersByName(String name, Pageable pageable){
        return userRepo.searchByFullName(name, pageable).map(userMapper::toAdminDTO);
    }

    @Override
    @Transactional
    public UserAdminDTO updateUserInfo(Long userId, UserAdminDTO dto){

        User existingUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setApproved(dto.isApproved());
        existingUser.setBanned(dto.isBanned());
        existingUser.setConfirmed(dto.isConfirmed());
        existingUser.setPersonalityScore(dto.getPersonalityScore());
        existingUser.setRole(dto.getRole());

        User saved = userRepo.save(existingUser);
        return userMapper.toAdminDTO(saved);

    }

}
