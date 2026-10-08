package com.role.implementation.adminpolicy.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.role.implementation.adminpolicy.model.EnergyPolicy;
import com.role.implementation.adminpolicy.model.PolicyEnforcementLog;
import com.role.implementation.adminpolicy.repository.EnergyPolicyRepository;
import com.role.implementation.adminpolicy.repository.PolicyEnforcementLogRepository;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.repository.DeviceRepository;
import com.role.implementation.energytracking.service.EnergyTrackingService;

@Service
public class EnergyPolicyEnforcementService {

    @Autowired
    private EnergyPolicyRepository policyRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EnergyTrackingService energyTrackingService;

    @Autowired
    private PolicyEnforcementLogRepository logRepository;

    /**
     * Runs every 1 minute
     */
    @Scheduled(fixedRate = 60000)
    public void enforcePolicies() {

        // ✅ Correct time separation
        LocalTime currentTime = LocalTime.now();      // policy window check
        LocalDateTime now = LocalDateTime.now();      // logging timestamp

        List<EnergyPolicy> activePolicies =
                policyRepository.findByEnabledTrue();

        for (EnergyPolicy policy : activePolicies) {

            // ⏱ Check policy active window
            if (currentTime.isBefore(policy.getStartTime())
                    || currentTime.isAfter(policy.getEndTime())) {
                continue;
            }

            List<Device> devices = deviceRepository.findAll();

            for (Device device : devices) {

                double todayEnergy =
                        energyTrackingService.getTodayEnergyForDevice(device.getId());

                // 🔍 Threshold check
                if (todayEnergy > policy.getEnergyThreshold()
                        && device.isStatus()) {

                    // 🔴 TURN DEVICE OFF
                    device.setStatus(false);
                    deviceRepository.save(device);

                    // 📝 LOG POLICY ENFORCEMENT
                    PolicyEnforcementLog log = new PolicyEnforcementLog();
                    log.setPolicyName(policy.getPolicyName());
                    log.setDeviceName(device.getName());

                    // If your Device has User relation, adjust accordingly
                    log.setUserName(
                            device.getUser() != null
                                    ? device.getUser().getName()
                                    : "UNKNOWN"
                    );

                    log.setEnergyConsumed(todayEnergy);
                    log.setThreshold(policy.getEnergyThreshold());
                    log.setEnforcedAt(now);

                    logRepository.save(log);
                }
            }
        }
    }
}
