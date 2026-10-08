package com.role.implementation.adminpolicy.scheduler;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.role.implementation.adminpolicy.model.EnergyPolicy;
import com.role.implementation.adminpolicy.repository.EnergyPolicyRepository;

@Component
public class PolicyEnforcementScheduler {

    @Autowired
    private EnergyPolicyRepository policyRepository;

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    public void enforcePolicies() {

        LocalTime now = LocalTime.now();

        List<EnergyPolicy> activePolicies =
                policyRepository.findByEnabledTrue();

        for (EnergyPolicy policy : activePolicies) {

            if (now.isBefore(policy.getStartTime()) ||
                now.isAfter(policy.getEndTime())) {
                continue;
            }

            // 🔥 NEXT: device enforcement logic goes here
            System.out.println(
                "Enforcing policy: " + policy.getPolicyName()
            );
        }
    }
}
