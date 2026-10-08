package com.role.implementation.analytics.service;

import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.service.DeviceService;
import com.role.implementation.energytracking.repository.EnergyUsageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class AnalyticsService {

    private final EnergyUsageRepository energyRepo;
    private final DeviceService deviceService;

    public AnalyticsService(EnergyUsageRepository energyRepo,
                            DeviceService deviceService) {
        this.energyRepo = energyRepo;
        this.deviceService = deviceService;
    }

    // ================= USER — LAST 7 DAYS ENERGY =================
    public Map<String, Double> getLast7DaysEnergy(Integer userId) {

        Map<String, Double> dailyEnergy = new LinkedHashMap<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);

            Double energy = energyRepo.getTotalEnergyForUserBetween(userId, start, end);

            String dayLabel = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            dailyEnergy.put(dayLabel, energy != null ? energy : 0.0);
        }

        return dailyEnergy;
    }

    // ================= USER — DEVICE COMPARISON (TODAY) =================
    public Map<String, Double> getDeviceComparisonToday() {

        Map<String, Double> map = new LinkedHashMap<>();
        List<Device> devices = deviceService.getDevicesForLoggedUser();

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        for (Device device : devices) {
            Double energy = energyRepo.getTotalEnergyForDeviceBetween(device, start, end);
            map.put(device.getName(), energy != null ? energy : 0.0);
        }

        return map;
    }

    // ================= USER — LAST 6 MONTHS ENERGY TREND =================
    public Map<String, Double> getLast6MonthsEnergy(Integer userId) {

        Map<String, Double> monthlyEnergy = new LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);

            LocalDateTime start = date.withDayOfMonth(1).atStartOfDay();
            LocalDateTime end = date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59, 59);

            Double energy = energyRepo.getTotalEnergyForUserBetween(userId, start, end);

            String label = date.getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            monthlyEnergy.put(label, energy != null ? energy : 0.0);
        }

        return monthlyEnergy;
    }

    // ================= USER — PEAK USAGE DAY =================
    public String getPeakUsageDay(Integer userId) {

        double maxEnergy = 0;
        String peakDay = "N/A";

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);

            Double energy = energyRepo.getTotalEnergyForUserBetween(userId, start, end);
            double value = energy != null ? energy : 0.0;

            if (value > maxEnergy) {
                maxEnergy = value;
                peakDay = date.getDayOfWeek()
                        .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            }
        }

        return peakDay + " (" + String.format("%.2f", maxEnergy) + " kWh)";
    }

    // ================= USER — ENERGY SAVING RECOMMENDATIONS =================
    public List<String> getEnergySavingRecommendations(Integer userId) {

        List<String> tips = new ArrayList<>();

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        Double energyObj = energyRepo.getTotalEnergyForUserBetween(userId, start, end);
        double todayEnergy = energyObj != null ? energyObj : 0.0;

        if (todayEnergy > 8) {
            tips.add("Your energy usage is very high today. Try reducing AC or heater usage.");
        } else if (todayEnergy > 5) {
            tips.add("Moderate energy usage detected. Turning off unused appliances can help save power.");
        } else {
            tips.add("Great job! Your energy usage is efficient today.");
        }

        tips.add("Switch off devices completely instead of standby mode.");
        tips.add("Use natural daylight instead of artificial lighting.");
        tips.add("Energy-efficient appliances reduce electricity bills.");
        tips.add("Unplug chargers when not in use to avoid phantom loads.");

        return tips;
    }

    // ================= ADMIN — TOP 5 DEVICES TODAY =================
    public Map<String, Double> getTop5DevicesToday() {

        Map<String, Double> map = new LinkedHashMap<>();

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        List<Object[]> results =
                energyRepo.findTopEnergyConsumingDevices(start, end, PageRequest.of(0, 5));

        for (Object[] row : results) {
            String deviceName = (String) row[0];
            Double energy = (Double) row[1];
            map.put(deviceName, energy != null ? energy : 0.0);
        }

        return map;
    }
}
