package com.role.implementation.energytracking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.repository.DeviceRepository;
import com.role.implementation.energytracking.model.EnergyUsage;
import com.role.implementation.energytracking.repository.EnergyUsageRepository;

@Service
public class EnergyTrackingService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EnergyUsageRepository energyUsageRepository;

    // =========================================================
    // ⏱ ENERGY TRACKING — EXISTING LOGIC (UNCHANGED)
    // =========================================================

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void calculateEnergyUsage() {

        List<Device> devices = deviceRepository.findAll();

        for (Device device : devices) {

            // 🛑 Skip invalid or deleted devices
            if (device == null || device.getId() == null) {
                continue;
            }

            // ✅ Track only ON devices
            if (device.isStatus()) {

                // ⚡ Energy (kWh) = Power (W) / 1000 × Time (hours)
                double powerInKW = device.getPowerRating() / 1000.0;
                double energyUsed = powerInKW * (1.0 / 60.0); // 1 minute

                EnergyUsage usage = new EnergyUsage();
                usage.setDevice(device);
                usage.setEnergyConsumed(energyUsed);
                usage.setTimestamp(LocalDateTime.now());

                energyUsageRepository.save(usage);
            }
        }
    }

    // =========================================================
    // ✅ ADMIN POLICY SUPPORT — REQUIRED METHOD
    // =========================================================

    /**
     * Returns today's total energy usage for a device.
     * Used by Admin Energy Policies.
     */
    public double getTodayEnergyForDevice(Long deviceId) {

        return energyUsageRepository
                .findTodayEnergyByDeviceId(deviceId)
                .orElse(0.0);
    }
}
