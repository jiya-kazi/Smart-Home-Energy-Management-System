package com.role.implementation.adminpolicy.model;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "policy_enforcement_logs")
public class PolicyEnforcementLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyName;
    private String deviceName;
    private String userName;

    private double energyConsumed;
    private double threshold;

    private LocalDateTime enforcedAt;

    /* ========= Getters & Setters ========= */

    public Long getId() { return id; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public double getEnergyConsumed() { return energyConsumed; }
    public void setEnergyConsumed(double energyConsumed) { this.energyConsumed = energyConsumed; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public LocalDateTime getEnforcedAt() { return enforcedAt; }
    public void setEnforcedAt(LocalDateTime enforcedAt) { this.enforcedAt = enforcedAt; }
}
