package com.role.implementation.automation.repository;

import com.role.implementation.automation.model.DeviceSchedule;
import com.role.implementation.devicemanagement.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceScheduleRepository extends JpaRepository<DeviceSchedule, Long> {

    // 🔹 Get all ACTIVE schedules (used by automation scheduler)
    List<DeviceSchedule> findByEnabledTrue();

    // 🔹 Delete schedules linked to a device (prevents FK errors when deleting device/user)
    void deleteByDevice(Device device);
}
