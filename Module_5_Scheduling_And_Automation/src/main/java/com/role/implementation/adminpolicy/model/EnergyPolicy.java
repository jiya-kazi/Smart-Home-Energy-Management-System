package com.role.implementation.adminpolicy.model;

import java.time.LocalTime;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "energy_policies")
public class EnergyPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Human-readable policy name
    @NotBlank(message = "Policy name is required")
    @Column(nullable = false, length = 100)
    private String policyName;

    // Time window when policy is active
    @NotNull(message = "Start time is required")
    @DateTimeFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @DateTimeFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime endTime;

    // Energy threshold in kWh
    @Positive(message = "Energy threshold must be greater than 0")
    @Column(nullable = false)
    private double energyThreshold;

    // ALL_USERS or HIGH_USAGE_USERS
    @NotBlank(message = "Scope is required")
    @Column(nullable = false)
    private String scope;

    // Enable / Disable policy
    @Column(nullable = false)
    private boolean enabled = true;

    /* ================= Constructors ================= */

    public EnergyPolicy() {
    }

    /* ================= Getters & Setters ================= */

    public Long getId() {
        return id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public double getEnergyThreshold() {
        return energyThreshold;
    }

    public void setEnergyThreshold(double energyThreshold) {
        this.energyThreshold = energyThreshold;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
