package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.PasswordChangeRequest;
import com.datingapp.backend.exception.UniqueConstraintViolationException;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ManagerRepository;
import com.datingapp.backend.repository.ModeratorRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final ModeratorRepository moderatorRepo;
    private final ManagerRepository managerRepo;
    private final UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AdminServiceImpl(ModeratorRepository moderatorRepo,
                            ManagerRepository managerRepo,
                            UserRepository userRepo) {
        this.moderatorRepo = moderatorRepo;
        this.managerRepo = managerRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Moderator hireModerator(Moderator moderator) {

        Optional<Moderator> byEmail = moderatorRepo.findByEmail(moderator.getEmail());
        if (byEmail.isPresent()) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        // Phone kontrolü
        Optional<Moderator> byPhone = moderatorRepo.findByPhone(moderator.getPhone());
        if (byPhone.isPresent()) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        return moderatorRepo.save(moderator);
    }

    @Override
    public void fireModerator(Long moderatorId) {
        moderatorRepo.deleteById(moderatorId);
    }

    @Override
    public Moderator updateModerator(Long moderatorId, Moderator moderator) {
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
        existing.setFullName(moderator.getFullName());
        existing.setEmail(moderator.getEmail());
        existing.setPhone(moderator.getPhone());
        if (moderator.getImageUrl() != null) {
            existing.setImageUrl(moderator.getImageUrl());
        }
        return moderatorRepo.save(existing);
    }

    @Override
    public List<Moderator> listAllModerators() {
        return moderatorRepo.findAll();
    }

    @Override
    public Moderator getModeratorById(Long id) {
        return moderatorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Moderator not found with id: " + id));
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
    public Manager hireManager(Manager manager) {

        Optional<Manager> byEmail = managerRepo.findByEmail(manager.getEmail());
        if (byEmail.isPresent()) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        // Phone kontrolü
        Optional<Manager> byPhone = managerRepo.findByPhone(manager.getPhone());
        if (byPhone.isPresent()) {
            throw new UniqueConstraintViolationException("Phone number is already in use");
        }

        return managerRepo.save(manager);
    }

    @Override
    public void fireManager(Long managerId) {
        managerRepo.deleteById(managerId);
    }

    @Override
    public Manager updateManager(Long managerId, Manager manager) {
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

        existing.setFullName(manager.getFullName());
        existing.setEmail(manager.getEmail());
        existing.setPhone(manager.getPhone());
        existing.setRole(manager.getRole());
        if (manager.getImageUrl() != null) {
            existing.setImageUrl(manager.getImageUrl());
        }
        return managerRepo.save(existing);
    }

    @Override
    public List<Manager> listAllManagers() {
        return managerRepo.findAll();
    }

    @Override
    public Manager getManagerById(Long id) {
        return managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id));
    }

    @Override
    public List<User> searchUsersByName(String name) {
        return userRepo.findByFullNameContainingIgnoreCase(name);
    }

    @Override
    public User updateUserInfo(Long userId, User user) {
        User existing = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setFullName(user.getFullName());
        existing.setLocation(user.getLocation());
        existing.setShorterbio(user.getShorterbio());
        // şifre hariç diğer alanlar...
        return userRepo.save(existing);
    }
}
