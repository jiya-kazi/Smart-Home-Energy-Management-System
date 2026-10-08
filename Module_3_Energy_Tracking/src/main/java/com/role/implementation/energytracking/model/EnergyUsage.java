package com.role.implementation.energytracking.model;

import java.time.LocalDateTime;
import javax.persistence.*;

import com.role.implementation.devicemanagement.model.Device;

@Entity
@Table(name = "energy_usage")
public class EnergyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Link energy record to a device
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    // ⚡ Energy consumed in kWh
    @Column(nullable = false)
    private double energyConsumed;

    // 🕒 Time of reading
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // ===== Constructors =====
    public EnergyUsage() {
    }

    public EnergyUsage(Device device, double energyConsumed, LocalDateTime timestamp) {
        this.device = device;
        this.energyConsumed = energyConsumed;
        this.timestamp = timestamp;
    }

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

    public double getEnergyConsumed() {
        return energyConsumed;
    }

    public void setEnergyConsumed(double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
