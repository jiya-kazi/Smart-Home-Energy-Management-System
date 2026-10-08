package com.role.implementation.analytics.controller;

import com.role.implementation.analytics.service.AnalyticsService;
import com.role.implementation.repository.UserRepository;
import com.role.implementation.model.User;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public AnalyticsController(AnalyticsService analyticsService,
                               UserRepository userRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/analytics")
    public String showAnalytics(Model model) {

        // 🔐 Get logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "redirect:/login";
        }

        Integer userId = user.getId();

        // 📅 Weekly Energy Trend
        Map<String, Double> weeklyEnergy = analyticsService.getLast7DaysEnergy(userId);
        model.addAttribute("weeklyEnergy", weeklyEnergy != null ? weeklyEnergy : Map.of());

        // 📊 Device Comparison Today
        Map<String, Double> deviceComparison = analyticsService.getDeviceComparisonToday();
        model.addAttribute("deviceComparison", deviceComparison != null ? deviceComparison : Map.of());

        // 📆 Monthly Energy Trend
        Map<String, Double> monthlyEnergy = analyticsService.getLast6MonthsEnergy(userId);
        model.addAttribute("monthlyEnergy", monthlyEnergy != null ? monthlyEnergy : Map.of());

        // 🔥 Peak Usage Day
        String peakDay = analyticsService.getPeakUsageDay(userId);
        model.addAttribute("peakDay", peakDay != null ? peakDay : "No data available");

        // 💡 ENERGY SAVING RECOMMENDATIONS (CRASH-SAFE FIX)
        List<String> energyTips = analyticsService.getEnergySavingRecommendations(userId);

        if (energyTips == null) {
            energyTips = new ArrayList<>();
        }

        // Ensure at least one message exists for UI logic
        if (energyTips.isEmpty()) {
            energyTips.add("Your energy usage data is being analyzed. Check back later for smart recommendations.");
        }

        model.addAttribute("energyTips", energyTips);

        // 👤 User name for header display
        model.addAttribute("userDetails", user.getName());

        return "analytics";
    }
}
