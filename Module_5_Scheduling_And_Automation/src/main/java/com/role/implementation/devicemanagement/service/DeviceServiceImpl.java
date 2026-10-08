package com.role.implementation.devicemanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.repository.DeviceRepository;
import com.role.implementation.energytracking.repository.EnergyUsageRepository;
import com.role.implementation.automation.repository.DeviceScheduleRepository;
import com.role.implementation.model.Role;
import com.role.implementation.model.User;
import com.role.implementation.repository.UserRepository;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepo;
    private final UserRepository userRepo;
    private final EnergyUsageRepository energyUsageRepo;
    private final DeviceScheduleRepository deviceScheduleRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepo,
                             UserRepository userRepo,
                             EnergyUsageRepository energyUsageRepo,
                             DeviceScheduleRepository deviceScheduleRepository) {
        this.deviceRepo = deviceRepo;
        this.userRepo = userRepo;
        this.energyUsageRepo = energyUsageRepo;
        this.deviceScheduleRepository = deviceScheduleRepository;
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email);
    }

    private boolean isAdmin(User user) {
        for (Role role : user.getRole()) {
            if (role.getRole().equals("ADMIN")) return true;
        }
        return false;
    }

    @Override
    public void addDevice(Device device) {
        device.setUser(getLoggedInUser());
        device.setStatus(false);
        deviceRepo.save(device);
    }

    @Override
    public List<Device> getDevicesForLoggedUser() {
        User user = getLoggedInUser();
        return isAdmin(user) ? deviceRepo.findAll() : deviceRepo.findByUser(user);
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepo.findAll();
    }

    @Override
    public void toggleDevice(Long id) {
        Device d = deviceRepo.findById(id).orElse(null);
        User user = getLoggedInUser();

        if (d != null && (isAdmin(user) || d.getUser().getId() == user.getId())) {
            d.setStatus(!d.isStatus());
            deviceRepo.save(d);
        }
    }

    // ✅ SAFE DELETE WITH FULL PROTECTION
    @Override
    @Transactional
    public void deleteDevice(Long id) {

        Device d = deviceRepo.findById(id).orElse(null);
        User user = getLoggedInUser();

        if (d != null && (isAdmin(user) || d.getUser().getId() == user.getId())) {

            // 🛑 Step 1: Turn OFF device so scheduler stops tracking it
            d.setStatus(false);
            deviceRepo.save(d);

            // 🧹 Step 2: Delete dependent child records FIRST
            energyUsageRepo.deleteByDevice(d);
            deviceScheduleRepository.deleteByDevice(d);

            // ❌ Step 3: Now delete the device itself
            deviceRepo.delete(d);
        }
    }

    @Override
    public long countTotalDevices() {
        User user = getLoggedInUser();
        return isAdmin(user) ? deviceRepo.count() : deviceRepo.countByUser(user);
    }

    @Override
    public long countActiveDevices() {
        User user = getLoggedInUser();
        return isAdmin(user)
                ? deviceRepo.countByStatus(true)
                : deviceRepo.countByUserAndStatus(user, true);
    }

    @Override
    public Map<Long, Double> getTodayEnergyForDevices(List<Device> devices) {
        Map<Long, Double> energyMap = new HashMap<>();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        for (Device device : devices) {
            Double energy = energyUsageRepo.getTotalEnergyForDeviceBetween(device, start, end);
            energyMap.put(device.getId(), energy != null ? energy : 0.0);
        }
        return energyMap;
    }

    @Override
    public Map<Long, String> getTodayUsageLevelForDevices(List<Device> devices) {
        Map<Long, String> usageMap = new HashMap<>();
        Map<Long, Double> energyMap = getTodayEnergyForDevices(devices);

        for (Device device : devices) {
            double energy = energyMap.getOrDefault(device.getId(), 0.0);

            if (energy < 0.5) usageMap.put(device.getId(), "LOW");
            else if (energy < 1.5) usageMap.put(device.getId(), "MEDIUM");
            else usageMap.put(device.getId(), "HIGH");
        }
        return usageMap;
    }

    @Override
    public Map<Long, Boolean> getTodayPeakUsageAlert(List<Device> devices) {
        Map<Long, Boolean> alertMap = new HashMap<>();
        Map<Long, Double> energyMap = getTodayEnergyForDevices(devices);

        for (Device device : devices) {
            double energy = energyMap.getOrDefault(device.getId(), 0.0);
            alertMap.put(device.getId(), energy > 2.5);
        }
        return alertMap;
    }
}
