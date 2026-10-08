package com.role.implementation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.role.implementation.model.User;
import com.role.implementation.repository.UserRepository;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.service.DeviceService;
import com.role.implementation.energytracking.service.EnergyService;
import com.role.implementation.energytracking.service.PdfReportService;
import com.role.implementation.service.DefaultUserService;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/adminScreen")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private DeviceService deviceService;
    @Autowired private EnergyService energyService;
    @Autowired private PdfReportService pdfReportService;

    // ✅ SERVICE that handles SAFE DELETE with TRANSACTION
    @Autowired private DefaultUserService defaultUserService;

    // =====================================================
    // 👑 ADMIN DASHBOARD
    // =====================================================
    @GetMapping
    public String displayDashboard(Model model) {

        model.addAttribute("userDetails", returnUsername());

        long totalUsers = userRepository.count();
        long totalDevices = deviceService.countTotalDevices();
        long activeDevices = deviceService.countActiveDevices();
        long inactiveDevices = totalDevices - activeDevices;

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalDevices", totalDevices);
        model.addAttribute("activeDevices", activeDevices);
        model.addAttribute("inactiveDevices", inactiveDevices);

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        Map<Integer, Double> userEnergyMap = new HashMap<>();
        Map<Integer, Boolean> userPeakMap = new HashMap<>();
        List<User> highUsageUsers = new ArrayList<>();

        for (User u : users) {
            double totalEnergy = energyService.getTotalEnergyForUser(u.getId());
            userEnergyMap.put(u.getId(), totalEnergy);

            double todayEnergy = energyService.getTodayEnergyForUser(u.getId());
            boolean isHigh = todayEnergy > 5.0;

            userPeakMap.put(u.getId(), isHigh);
            if (isHigh) highUsageUsers.add(u);
        }

        model.addAttribute("userEnergyMap", userEnergyMap);
        model.addAttribute("userPeakMap", userPeakMap);
        model.addAttribute("highUsageUsers", highUsageUsers);

        return "adminScreen";
    }

    // =====================================================
    // 🏠 VIEW ALL DEVICES
    // =====================================================
    @GetMapping("/devices")
    public String viewAllDevices(Model model) {

        List<Device> devices = deviceService.getAllDevices();
        model.addAttribute("devices", devices);

        Map<Long, Double> deviceEnergyMap = new HashMap<>();
        Map<Long, String> deviceUsageLevelMap = new HashMap<>();

        List<Device> sortedDevices = new ArrayList<>(devices);
        sortedDevices.sort((d1, d2) -> Double.compare(
                energyService.getTodayEnergyForDevice(d2),
                energyService.getTodayEnergyForDevice(d1)
        ));

        List<String> deviceNames = new ArrayList<>();
        List<Double> deviceEnergyValues = new ArrayList<>();

        for (Device d : devices) {
            double energy = energyService.getTodayEnergyForDevice(d);
            deviceEnergyMap.put(d.getId(), energy);
            deviceUsageLevelMap.put(d.getId(), energyService.getUsageLevel(energy));
        }

        for (Device d : sortedDevices) {
            deviceNames.add(d.getName());
            deviceEnergyValues.add(energyService.getTodayEnergyForDevice(d));
        }

        model.addAttribute("deviceEnergyMap", deviceEnergyMap);
        model.addAttribute("deviceUsageLevelMap", deviceUsageLevelMap);
        model.addAttribute("deviceNames", deviceNames);
        model.addAttribute("deviceEnergyValues", deviceEnergyValues);

        return "adminDevices";
    }

    // =====================================================
    // 📄 DEVICE REPORT PDF
    // =====================================================
    @GetMapping("/devices/report")
    public void downloadDeviceEnergyReport(HttpServletResponse response) throws Exception {

        List<Device> devices = deviceService.getAllDevices();

        Map<Long, Double> deviceEnergyMap = new HashMap<>();
        Map<Long, String> deviceUsageLevelMap = new HashMap<>();

        for (Device d : devices) {
            double energy = energyService.getTodayEnergyForDevice(d);
            deviceEnergyMap.put(d.getId(), energy);
            deviceUsageLevelMap.put(d.getId(), energyService.getUsageLevel(energy));
        }

        pdfReportService.generateDeviceEnergyReport(response, devices, deviceEnergyMap, deviceUsageLevelMap);
    }

    // =====================================================
    // 👤 USER REPORT PDF
    // =====================================================
    @GetMapping("/users/report")
    public void downloadUserEnergyReport(HttpServletResponse response) throws Exception {

        List<User> users = userRepository.findAll();

        Map<Integer, Double> userEnergyMap = new HashMap<>();
        Map<Integer, Double> userCostMap = new HashMap<>();

        for (User user : users) {
            double energy = energyService.getTodayEnergyForUser(user.getId());
            userEnergyMap.put(user.getId(), energy);
            userCostMap.put(user.getId(), energyService.calculateCost(energy));
        }

        pdfReportService.generateUserEnergyReport(response, users, userEnergyMap, userCostMap);
    }

    // =====================================================
    // ❌ SAFE DELETE USER (NOW CORRECT)
    // =====================================================
    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Integer id) {

        // 🔥 Delegating delete to service layer (transactional)
        defaultUserService.deleteUserCompletely(id);

        return "redirect:/adminScreen";
    }

    // =====================================================
    // 🔐 Get Logged-in Admin Name
    // =====================================================
    private String returnUsername() {
        UserDetails user = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User users = userRepository.findByEmail(user.getUsername());
        return (users != null) ? users.getName() : user.getUsername();
    }
}
