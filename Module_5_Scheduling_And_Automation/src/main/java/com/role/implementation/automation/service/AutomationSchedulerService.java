package com.role.implementation.automation.service;

import com.role.implementation.automation.model.DeviceSchedule;
import com.role.implementation.automation.repository.DeviceScheduleRepository;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.repository.DeviceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class AutomationSchedulerService {

    private final DeviceScheduleRepository scheduleRepo;
    private final DeviceRepository deviceRepo;

    public AutomationSchedulerService(DeviceScheduleRepository scheduleRepo,
                                      DeviceRepository deviceRepo) {
        this.scheduleRepo = scheduleRepo;
        this.deviceRepo = deviceRepo;
    }

    // Runs every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void checkAndRunSchedules() {

        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        List<DeviceSchedule> schedules = scheduleRepo.findByEnabledTrue();

        for (DeviceSchedule schedule : schedules) {

            Device device = schedule.getDevice();

            // Turn ON device
            if (schedule.getOnTime().equals(now) && !device.isStatus()) {
                device.setStatus(true);
                deviceRepo.save(device);
                System.out.println("AUTO ON → " + device.getName());
            }

            // Turn OFF device
            if (schedule.getOffTime().equals(now) && device.isStatus()) {
                device.setStatus(false);
                deviceRepo.save(device);
                System.out.println("AUTO OFF → " + device.getName());
            }
        }
    }
}
