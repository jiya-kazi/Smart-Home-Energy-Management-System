package com.role.implementation.automation.controller;

import com.role.implementation.automation.model.DeviceSchedule;
import com.role.implementation.automation.repository.DeviceScheduleRepository;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.service.DeviceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final DeviceService deviceService;
    private final DeviceScheduleRepository scheduleRepo;

    public ScheduleController(DeviceService deviceService,
                              DeviceScheduleRepository scheduleRepo) {
        this.deviceService = deviceService;
        this.scheduleRepo = scheduleRepo;
    }

    // Show schedule page
    @GetMapping
    public String schedulePage(Model model) {
        List<Device> devices = deviceService.getDevicesForLoggedUser();

        model.addAttribute("devices", devices);
        model.addAttribute("schedule", new DeviceSchedule());
        model.addAttribute("schedules", scheduleRepo.findAll());

        return "schedules";
    }

    // Add new schedule
    @PostMapping("/add")
    public String addSchedule(@ModelAttribute DeviceSchedule schedule) {
        scheduleRepo.save(schedule);
        return "redirect:/schedules";
    }

    // Enable/Disable schedule
    @GetMapping("/toggle/{id}")
    public String toggleSchedule(@PathVariable Long id) {
        DeviceSchedule schedule = scheduleRepo.findById(id).orElse(null);
        if (schedule != null) {
            schedule.setEnabled(!schedule.isEnabled());
            scheduleRepo.save(schedule);
        }
        return "redirect:/schedules";
    }

    // Delete schedule
    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleRepo.deleteById(id);
        return "redirect:/schedules";
    }
}
