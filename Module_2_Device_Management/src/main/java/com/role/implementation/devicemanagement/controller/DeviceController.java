package com.role.implementation.devicemanagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.devicemanagement.service.DeviceService;

@Controller
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // ================= COMMON METHOD TO LOAD PAGE DATA =================
    private void loadDevicePageData(Model model) {
        List<Device> devices = deviceService.getDevicesForLoggedUser();
        model.addAttribute("devices", devices);
        model.addAttribute("device", new Device());

        // Energy usage
        Map<Long, Double> deviceEnergyMap = deviceService.getTodayEnergyForDevices(devices);
        if (deviceEnergyMap == null) deviceEnergyMap = new HashMap<>();
        model.addAttribute("deviceEnergyMap", deviceEnergyMap);

        // Usage level
        Map<Long, String> deviceUsageLevelMap = deviceService.getTodayUsageLevelForDevices(devices);
        if (deviceUsageLevelMap == null) deviceUsageLevelMap = new HashMap<>();
        model.addAttribute("deviceUsageLevelMap", deviceUsageLevelMap);

        // Peak alert
        Map<Long, Boolean> devicePeakAlertMap = deviceService.getTodayPeakUsageAlert(devices);
        if (devicePeakAlertMap == null) devicePeakAlertMap = new HashMap<>();
        model.addAttribute("devicePeakAlertMap", devicePeakAlertMap);

        // Cost calculation
        Map<Long, Double> deviceCostMap = new HashMap<>();
        double ratePerUnit = 8.0;

        for (Map.Entry<Long, Double> entry : deviceEnergyMap.entrySet()) {
            deviceCostMap.put(entry.getKey(), entry.getValue() * ratePerUnit);
        }

        model.addAttribute("deviceCostMap", deviceCostMap);
    }

    // ================= USER DEVICES PAGE =================
    @GetMapping
    public String devicesPage(Model model) {
        loadDevicePageData(model);
        return "devices";
    }

    // ================= ADD NEW DEVICE =================
    @PostMapping("/add")
    public String addDevice(@Valid @ModelAttribute("device") Device device,
                            BindingResult result,
                            @RequestParam(required = false) String customName,
                            @RequestParam(required = false) String customType,
                            @RequestParam(required = false) String customLocation,
                            Model model) {

        if (result.hasErrors()) {
            loadDevicePageData(model); // 🔥 CRITICAL FIX
            return "devices";
        }

        if ("Other".equals(device.getName()) && customName != null && !customName.isBlank()) {
            device.setName(customName);
        }

        if ("Other".equals(device.getType()) && customType != null && !customType.isBlank()) {
            device.setType(customType);
        }

        if ("Other".equals(device.getLocation()) && customLocation != null && !customLocation.isBlank()) {
            device.setLocation(customLocation);
        }

        device.setStatus(false);
        deviceService.addDevice(device);

        return "redirect:/devices?success";
    }

    // ================= USER TOGGLE =================
    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id) {
        deviceService.toggleDevice(id);
        return "redirect:/devices";
    }

    // ================= USER DELETE =================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return "redirect:/devices";
    }

    // ================= ADMIN CONTROLS =================
    @GetMapping("/admin/toggle/{id}")
    public String adminToggle(@PathVariable Long id) {
        deviceService.toggleDevice(id);
        return "redirect:/adminScreen/devices";
    }

    @GetMapping("/admin/delete/{id}")
    public String adminDelete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return "redirect:/adminScreen/devices";
    }
}
