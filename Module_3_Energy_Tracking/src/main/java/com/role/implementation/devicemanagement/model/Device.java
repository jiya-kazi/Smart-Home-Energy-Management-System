package com.role.implementation.devicemanagement.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.role.implementation.model.User;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= DEVICE NAME =================
    @NotBlank(message = "Device name is required")
    @Size(max = 50, message = "Device name cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    // ================= DEVICE TYPE =================
    @NotBlank(message = "Device type is required")
    @Size(max = 40, message = "Device type cannot exceed 40 characters")
    @Column(nullable = false, length = 40)
    private String type;

    // ================= LOCATION =================
    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String location;

    // ================= POWER RATING =================
    @Min(value = 1, message = "Power rating must be greater than 0")
    @Max(value = 10000, message = "Power rating is too large")
    @Column(nullable = false)
    private double powerRating;

    // ================= STATUS =================
    @Column(nullable = false)
    private boolean status = false; // Default OFF

    // ================= OWNER =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Device() {}

    // ================= GETTERS & SETTERS =================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public double getPowerRating() { return powerRating; }
    public void setPowerRating(double powerRating) { this.powerRating = powerRating; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // 🔥 USER-FRIENDLY POWER USAGE LEVEL (Not stored in DB)
    @Transient
    public String getUsageLevel() {
        if (this.powerRating < 200) {
            return "LOW";        // Bulb, Fan, Router
        }
        else if (this.powerRating <= 1000) {
            return "MEDIUM";     // TV, Fridge, Computer
        }
        else {
            return "HIGH";       // AC, Heater, Washing Machine
        }
    }
}
