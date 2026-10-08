package com.role.implementation.adminpolicy.controller;

import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.role.implementation.adminpolicy.model.EnergyPolicy;
import com.role.implementation.adminpolicy.model.PolicyEnforcementLog;
import com.role.implementation.adminpolicy.repository.EnergyPolicyRepository;
import com.role.implementation.adminpolicy.repository.PolicyEnforcementLogRepository;

@Controller
public class AdminPolicyController {

    // =========================================================
    // DEPENDENCIES
    // =========================================================

    @Autowired
    private EnergyPolicyRepository policyRepository;

    @Autowired
    private PolicyEnforcementLogRepository policyEnforcementLogRepository;

    // =========================================================
    // SHOW POLICY PAGE (CREATE + LIST)
    // =========================================================
    @GetMapping("/admin/policies")
    public String showPolicies(Model model) {

        List<EnergyPolicy> policies =
                policyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        model.addAttribute("policies", policies);

        // Prevent overwriting form on validation error redirect
        if (!model.containsAttribute("policy")) {
            model.addAttribute("policy", new EnergyPolicy());
        }

        return "energy-policies";
    }

    // =========================================================
    // CREATE NEW POLICY (WITH VALIDATION)
    // =========================================================
    @PostMapping("/admin/policies/add")
    public String addPolicy(
            @Valid @ModelAttribute("policy") EnergyPolicy policy,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // ❌ Bean validation failed
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please fill all fields correctly (check time & threshold)."
            );
            redirectAttributes.addFlashAttribute("policy", policy);
            return "redirect:/admin/policies";
        }

        // ❌ Logical validation
        if (policy.getStartTime().isAfter(policy.getEndTime())) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Start time must be before end time."
            );
            redirectAttributes.addFlashAttribute("policy", policy);
            return "redirect:/admin/policies";
        }

        // ✅ Safe defaults
        policy.setEnabled(true);

        policyRepository.save(policy);

        redirectAttributes.addFlashAttribute(
                "success",
                "Energy policy created successfully."
        );

        return "redirect:/admin/policies";
    }

    // =========================================================
    // ENABLE / DISABLE POLICY
    // =========================================================
    @GetMapping("/admin/policies/toggle/{id}")
    public String togglePolicy(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        policyRepository.findById(id).ifPresent(policy -> {
            policy.setEnabled(!policy.isEnabled());
            policyRepository.save(policy);
        });

        redirectAttributes.addFlashAttribute(
                "success",
                "Policy status updated."
        );

        return "redirect:/admin/policies";
    }

    // =========================================================
    // READ-ONLY POLICY STATUS (AUTO-REFRESH SUPPORT)
    // =========================================================
    @GetMapping("/admin/policies/status")
    @ResponseBody
    public List<Long> getActivePolicyIds() {

        LocalTime now = LocalTime.now();

        return policyRepository.findByEnabledTrue()
                .stream()
                .filter(p ->
                        !now.isBefore(p.getStartTime()) &&
                        !now.isAfter(p.getEndTime())
                )
                .map(EnergyPolicy::getId)
                .toList();
    }

    // =========================================================
    // VIEW POLICY ENFORCEMENT LOGS (READ-ONLY)
    // =========================================================
    @GetMapping("/admin/policies/logs")
    public String viewPolicyLogs(Model model) {

        List<PolicyEnforcementLog> logs =
                policyEnforcementLogRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "enforcedAt")
                );

        model.addAttribute("logs", logs);

        return "policy-enforcement-logs";
    }
}
