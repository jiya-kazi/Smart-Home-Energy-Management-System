package com.role.implementation.devicemanagement.service;

import java.util.List;
import java.util.Map;

import com.role.implementation.devicemanagement.model.Device;

public interface DeviceService {

    // ================= DEVICE MANAGEMENT =================
    void addDevice(Device device);

    List<Device> getDevicesForLoggedUser();

    List<Device> getAllDevices();

    void toggleDevice(Long id);

    void deleteDevice(Long id);

    // ================= DASHBOARD COUNTS =================
    long countTotalDevices();

    long countActiveDevices();

    // ================= ENERGY ANALYTICS =================

    /**
     * Returns today's total energy consumption (kWh) for each device.
     * Map Key   → Device ID
     * Map Value → Energy consumed today (never null, defaults to 0.0)
     */
    Map<Long, Double> getTodayEnergyForDevices(List<Device> devices);

    /**
     * Returns usage level based on today's energy:
     * LOW, MEDIUM, HIGH (never null)
     */
    Map<Long, String> getTodayUsageLevelForDevices(List<Device> devices);

    /**
     * Returns whether a device crossed peak usage threshold today
     * true  → High usage alert
     * false → Normal
     */
    Map<Long, Boolean> getTodayPeakUsageAlert(List<Device> devices);
}
