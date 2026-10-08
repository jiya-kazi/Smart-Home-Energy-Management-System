package com.role.implementation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.role.implementation.devicemanagement.service.DeviceService;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.energytracking.service.EnergyService;
import com.role.implementation.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DeviceService deviceService;
    private final UserRepository userRepository;
    private final EnergyService energyService;

    public DashboardController(DeviceService deviceService,
                               UserRepository userRepository,
                               EnergyService energyService) {
        this.deviceService = deviceService;
        this.userRepository = userRepository;
        this.energyService = energyService;
    }

    @GetMapping
    public String displayDashboard(Model model) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String username = userRepository.findByEmail(email).getName();

        model.addAttribute("userDetails", username);

        // 🔹 Device info
        List<Device> userDevices = deviceService.getDevicesForLoggedUser();
        model.addAttribute("devices", userDevices);
        model.addAttribute("totalDevices", deviceService.countTotalDevices());
        model.addAttribute("activeDevices", deviceService.countActiveDevices());

        // ⚡ TOTAL ENERGY TODAY (ALL DEVICES)
        double todayEnergy = energyService.getTotalTodayEnergy(userDevices);
        double todayCost = energyService.calculateCost(todayEnergy);

        // 🟢 USER-FRIENDLY LEVEL FOR TOTAL USAGE
        String usageLevel = energyService.getUsageLevel(todayEnergy);
        String usageColor = energyService.getUsageColor(todayEnergy);

        model.addAttribute("todayEnergy", todayEnergy);
        model.addAttribute("todayCost", todayCost);
        model.addAttribute("usageLevel", usageLevel);
        model.addAttribute("usageColor", usageColor);

        // ⚡ ENERGY PER DEVICE (FOR TABLE DISPLAY)
        Map<Long, Double> deviceEnergyMap = new HashMap<>();
        Map<Long, String> deviceUsageLevelMap = new HashMap<>();

        for (Device device : userDevices) {

            double energyToday = energyService.getTodayEnergyForDevice(device);
            deviceEnergyMap.put(device.getId(), energyToday);

            String level = energyService.getUsageLevel(energyToday);
            deviceUsageLevelMap.put(device.getId(), level);
        }

        model.addAttribute("deviceEnergyMap", deviceEnergyMap);
        model.addAttribute("deviceUsageLevelMap", deviceUsageLevelMap);

        return "dashboard";
    }
}
