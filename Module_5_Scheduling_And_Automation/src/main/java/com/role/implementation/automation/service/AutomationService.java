package com.role.implementation.automation.service;

import com.role.implementation.automation.model.DeviceSchedule;
import com.role.implementation.automation.repository.DeviceScheduleRepository;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.repository.DeviceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class AutomationService {

    private final DeviceScheduleRepository scheduleRepo;
    private final DeviceRepository deviceRepo;

    public AutomationService(DeviceScheduleRepository scheduleRepo,
                             DeviceRepository deviceRepo) {
        this.scheduleRepo = scheduleRepo;
        this.deviceRepo = deviceRepo;
    }

    /**
     * ⏱ Runs every 1 minute
     * Checks all enabled schedules and updates device ON/OFF state
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void runSchedules() {

        // Current time rounded to minute
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        List<DeviceSchedule> schedules = scheduleRepo.findByEnabledTrue();

        for (DeviceSchedule schedule : schedules) {

            Device device = schedule.getDevice();

            // Skip if device was deleted or not loaded
            if (device == null) continue;

            // 🔵 TURN ON
            if (schedule.getOnTime() != null &&
                now.equals(schedule.getOnTime()) &&
                !device.isStatus()) {

                device.setStatus(true);
                deviceRepo.save(device);
                System.out.println("AUTO ON  → " + device.getName() + " at " + now);
            }

            // ⚫ TURN OFF
            if (schedule.getOffTime() != null &&
                now.equals(schedule.getOffTime()) &&
                device.isStatus()) {

                device.setStatus(false);
                deviceRepo.save(device);
                System.out.println("AUTO OFF → " + device.getName() + " at " + now);
            }
        }
    }
}
