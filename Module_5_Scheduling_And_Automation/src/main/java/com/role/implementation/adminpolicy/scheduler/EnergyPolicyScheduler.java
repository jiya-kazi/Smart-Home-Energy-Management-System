package com.role.implementation.adminpolicy.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.role.implementation.adminpolicy.service.EnergyPolicyService;

@Component
public class EnergyPolicyScheduler {

    @Autowired
    private EnergyPolicyService energyPolicyService;

    /**
     * Runs every 1 minute
     * Safe frequency for admin-level enforcement
     */
    @Scheduled(fixedRate = 60000)
    public void enforceAdminPolicies() {
        energyPolicyService.enforcePolicies();
    }
}
