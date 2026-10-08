package com.role.implementation.adminpolicy.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.role.implementation.adminpolicy.model.EnergyPolicy;
import com.role.implementation.adminpolicy.model.PolicyEnforcementLog;
import com.role.implementation.adminpolicy.repository.EnergyPolicyRepository;
import com.role.implementation.adminpolicy.repository.PolicyEnforcementLogRepository;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.repository.DeviceRepository;
import com.role.implementation.energytracking.service.EnergyTrackingService;

@Service
public class EnergyPolicyService {

    // =========================================================
    // DEPENDENCIES
    // =========================================================

    @Autowired
    private EnergyPolicyRepository policyRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EnergyTrackingService energyTrackingService;

    // ✅ NEW — POLICY ENFORCEMENT LOGS
    @Autowired
    private PolicyEnforcementLogRepository logRepository;

    // =========================================================
    // POLICY ENFORCEMENT ENTRY POINT
    // =========================================================

    /**
     * Evaluates and enforces all enabled admin energy policies.
     * This method is intended to be triggered by a scheduler.
     */
    public void enforcePolicies() {

        LocalTime now = LocalTime.now();
        List<EnergyPolicy> policies = policyRepository.findByEnabledTrue();

        for (EnergyPolicy policy : policies) {
            if (isWithinPolicyWindow(policy, now)) {
                applyPolicy(policy);
            }
        }
    }

    // =========================================================
    // INTERNAL HELPERS
    // =========================================================

    /**
     * Checks whether the current time falls within the policy time window.
     */
    private boolean isWithinPolicyWindow(EnergyPolicy policy, LocalTime now) {
        return !now.isBefore(policy.getStartTime())
            && !now.isAfter(policy.getEndTime());
    }

    /**
     * Applies a single admin energy policy across all devices.
     * Devices exceeding the configured energy threshold are forced OFF.
     * Each enforcement is logged once.
     */
    private void applyPolicy(EnergyPolicy policy) {

        List<Device> devices = deviceRepository.findAll();

        for (Device device : devices) {

            double todayEnergy =
                    energyTrackingService.getTodayEnergyForDevice(device.getId());

            // ✅ Enforce ONLY if device is ON (prevents duplicate logs)
            if (todayEnergy > policy.getEnergyThreshold() && device.isStatus()) {

                // 🔴 FORCE OFF
                device.setStatus(false);
                deviceRepository.save(device);

                // 🧾 SAVE ENFORCEMENT LOG
                PolicyEnforcementLog log = new PolicyEnforcementLog();
                log.setPolicyName(policy.getPolicyName());
                log.setDeviceName(device.getName());
                log.setUserName(device.getUser().getName());
                log.setEnergyConsumed(todayEnergy);
                log.setThreshold(policy.getEnergyThreshold());
                log.setEnforcedAt(LocalDateTime.now());

                logRepository.save(log);
            }
        }
    }
}
