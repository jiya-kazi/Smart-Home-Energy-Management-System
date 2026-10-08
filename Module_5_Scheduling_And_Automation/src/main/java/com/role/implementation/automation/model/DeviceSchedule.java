package com.role.implementation.automation.model;

import com.role.implementation.devicemanagement.model.Device;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
public class DeviceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    // Allows "15:30" from HTML to map into LocalTime
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime onTime;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime offTime;

    private boolean enabled = true;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public LocalTime getOnTime() {
        return onTime;
    }

    public void setOnTime(LocalTime onTime) {
        this.onTime = onTime;
    }

    public LocalTime getOffTime() {
        return offTime;
    }

    public void setOffTime(LocalTime offTime) {
        this.offTime = offTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
