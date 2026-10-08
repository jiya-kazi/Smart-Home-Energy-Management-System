package com.role.implementation.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.role.implementation.DTO.UserRegisteredDTO;
import com.role.implementation.model.Role;
import com.role.implementation.model.User;
import com.role.implementation.repository.RoleRepository;
import com.role.implementation.repository.UserRepository;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.repository.DeviceRepository;
import com.role.implementation.energytracking.repository.EnergyUsageRepository;
import com.role.implementation.automation.repository.DeviceScheduleRepository;

@Service
public class DefaultUserServiceImpl implements DefaultUserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    // 🔥 REQUIRED repositories for safe delete
    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private EnergyUsageRepository energyUsageRepo;

    @Autowired
    private DeviceScheduleRepository scheduleRepo;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // =====================================================
    // 🔐 SPRING SECURITY LOGIN
    // =====================================================
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRole())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    // =====================================================
    // 👤 USER REGISTRATION
    // =====================================================
    @Override
    public User save(UserRegisteredDTO userRegisteredDTO) {

        Role role;
        if ("ADMIN".equals(userRegisteredDTO.getRole())) {
            role = roleRepo.findByRole("ADMIN");
        } else {
            role = roleRepo.findByRole("USER");
        }

        User user = new User();
        user.setEmail(userRegisteredDTO.getEmail_id());
        user.setName(userRegisteredDTO.getName());
        user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
        user.setRole(role);

        return userRepo.save(user);
    }

    // =====================================================
    // ❌ SAFE USER DELETE (ADMIN ONLY)
    // =====================================================
    @Override
    @Transactional
    public void deleteUserCompletely(Integer userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1️⃣ Get all devices of the user
        for (Device device : deviceRepo.findByUser(user)) {

            // 2️⃣ Delete automation schedules
            scheduleRepo.deleteByDevice(device);

            // 3️⃣ Delete energy usage history
            energyUsageRepo.deleteByDevice(device);
        }

        // 4️⃣ Delete devices
        deviceRepo.deleteByUser(user);

        // 5️⃣ Finally delete user
        userRepo.delete(user);
    }
}
