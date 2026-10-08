package com.role.implementation.analytics.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.repository.DeviceRepository;
import com.role.implementation.energytracking.repository.EnergyUsageRepository;

@Service
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final EnergyUsageRepository energyRepo;
    private final DeviceRepository deviceRepo;

    public AdminAnalyticsServiceImpl(EnergyUsageRepository energyRepo,
                                     DeviceRepository deviceRepo) {
        this.energyRepo = energyRepo;
        this.deviceRepo = deviceRepo;
    }

    // ================= TOTAL SYSTEM ENERGY TODAY =================
    @Override
    public double getTotalEnergyToday() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        Double energy = energyRepo.getTotalEnergyBetween(start, LocalDateTime.now());
        return energy != null ? energy : 0.0;
    }

    // ================= TOTAL SYSTEM ENERGY THIS WEEK =================
    @Override
    public double getTotalEnergyThisWeek() {
        LocalDateTime start = LocalDate.now().minusDays(6).atStartOfDay();
        Double energy = energyRepo.getTotalEnergyBetween(start, LocalDateTime.now());
        return energy != null ? energy : 0.0;
    }

    // ================= SYSTEM ENERGY LAST 7 DAYS =================
    @Override
    public Map<String, Double> getSystemLast7DaysEnergy() {

        Map<String, Double> weeklyEnergy = new LinkedHashMap<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);

            Double energy = energyRepo.getTotalEnergyBetween(start, end);

            String label = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            weeklyEnergy.put(label, energy != null ? energy : 0.0);
        }

        return weeklyEnergy;
    }

    // ================= TOP 5 ENERGY CONSUMING DEVICES TODAY =================
    @Override
    public Map<String, Double> getTop5DevicesToday() {

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        List<Object[]> results =
                energyRepo.findTopEnergyConsumingDevices(start, end, PageRequest.of(0, 5));

        Map<String, Double> topDevices = new LinkedHashMap<>();

        for (Object[] row : results) {
            String deviceName = (String) row[0];
            Double energy = (Double) row[1];
            topDevices.put(deviceName, energy != null ? energy : 0.0);
        }

        return topDevices;
    }

    // ================= ACTIVE DEVICES COUNT =================
    @Override
    public long getActiveDeviceCount() {
        return deviceRepo.countByStatus(true);
    }

    // ================= USERS WITH PEAK DEVICE USAGE TODAY =================
    @Override
    public Map<Integer, Boolean> getUsersWithPeakUsage() {

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        double PEAK_THRESHOLD = 2.0;

        Map<Integer, Boolean> userPeakMap = new LinkedHashMap<>();
        List<Device> allDevices = deviceRepo.findAll();

        for (Device device : allDevices) {
            Integer userId = device.getUser().getId();
            Double energy = energyRepo.getTotalEnergyForDeviceBetween(device, start, end);
            double value = energy != null ? energy : 0.0;

            if (value >= PEAK_THRESHOLD) {
                userPeakMap.put(userId, true);
            } else {
                userPeakMap.putIfAbsent(userId, false);
            }
        }

        return userPeakMap;
    }

    // ================= SYSTEM RECOMMENDATIONS =================
    @Override
    public Map<String, String> getSystemRecommendations() {

        Map<String, String> recommendations = new LinkedHashMap<>();

        double todayEnergy = getTotalEnergyToday();
        double weekEnergy = getTotalEnergyThisWeek();
        long activeDevices = getActiveDeviceCount();
        Map<Integer, Boolean> peakUsers = getUsersWithPeakUsage();

        long highUsers = peakUsers.values().stream().filter(v -> v).count();

        if (todayEnergy > 50) {
            recommendations.put("High Energy Usage",
                    "System energy consumption is very high today. Consider notifying heavy users.");
        }

        if ((weekEnergy / 7) > todayEnergy * 0.8) {
            recommendations.put("Weekly Energy Spike",
                    "Weekly average energy usage is rising. Monitor device schedules.");
        }

        if (activeDevices > 20) {
            recommendations.put("Too Many Active Devices",
                    "Large number of devices are currently ON. Encourage power-saving practices.");
        }

        if (highUsers > 0) {
            recommendations.put("High Consumption Users",
                    highUsers + " user(s) show unusually high energy usage today.");
        }

        if (recommendations.isEmpty()) {
            recommendations.put("System Stable",
                    "Energy usage and device activity are within normal range.");
        }

        return recommendations;
    }
}
