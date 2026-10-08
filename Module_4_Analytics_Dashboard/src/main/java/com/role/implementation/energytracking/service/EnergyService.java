package com.role.implementation.energytracking.service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.energytracking.repository.EnergyUsageRepository;
import com.role.implementation.model.User;

@Service
public class EnergyService {

    @Autowired
    private EnergyUsageRepository energyUsageRepository;

    private static final double RATE_PER_UNIT = 6.0; // ₹6 per kWh

    // 🔹 Energy today per device
    public double getTodayEnergyForDevice(Device device) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        Double total = energyUsageRepository
                .getTotalEnergyForDeviceBetween(device, start, end);

        return total != null ? total : 0.0;
    }

    // 🔹 Energy today for all devices of a user
    public double getTotalTodayEnergy(List<Device> devices) {
        return devices.stream().mapToDouble(this::getTodayEnergyForDevice).sum();
    }

    // 👤 Energy today for a specific user (FIXED → Integer)
    public double getTodayEnergyForUser(Integer userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        Double total = energyUsageRepository.getTotalEnergyForUserBetween(userId, start, end);
        return total != null ? total : 0.0;
    }

    // 💰 Convert energy → cost
    public double calculateCost(double energyInKwh) {
        return energyInKwh * RATE_PER_UNIT;
    }

    // 👑 Admin — total energy used by a user (all time) (FIXED → Integer)
    public double getTotalEnergyForUser(Integer userId) {
        Double total = energyUsageRepository.getTotalEnergyByUserId(userId);
        return total != null ? total : 0.0;
    }

    // 🎯 Usage level
    public String getUsageLevel(double energyKwh) {
        if (energyKwh < 0.5) return "LOW";
        else if (energyKwh < 2.0) return "MEDIUM";
        else return "HIGH";
    }

    // 🎨 Usage color (for UI badges)
    public String getUsageColor(double energyKwh) {
        if (energyKwh < 0.5) return "green";
        else if (energyKwh < 2.0) return "orange";
        else return "red";
    }

    // 📄 DEVICE-WISE ADMIN PDF REPORT
    public void generateAdminEnergyReport(HttpServletResponse response,
                                          List<Device> devices,
                                          Map<Long, Double> energyMap,
                                          Map<Long, String> usageMap) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=SHEMS-Device-Energy-Report.pdf");

        Document document = new Document(PageSize.A4);
        OutputStream out = response.getOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12);
        Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);

        Paragraph system = new Paragraph("SHEMS – Smart Home Energy Management System", normalFont);
        system.setAlignment(Element.ALIGN_CENTER);
        document.add(system);

        Paragraph title = new Paragraph("Device Energy Usage Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(8);
        document.add(title);

        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        Paragraph generated = new Paragraph("Generated on: " + date, normalFont);
        generated.setAlignment(Element.ALIGN_CENTER);
        generated.setSpacingAfter(15);
        document.add(generated);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        Stream.of("Device Name", "Type", "Power (W)", "Energy Today (kWh)", "Usage Level")
                .forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Paragraph(header, boldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8);
                    table.addCell(cell);
                });

        for (Device d : devices) {
            table.addCell(d.getName());
            table.addCell(d.getType());
            table.addCell(String.valueOf(d.getPowerRating()));
            table.addCell(String.format("%.3f", energyMap.get(d.getId())));
            table.addCell(usageMap.get(d.getId()));
        }

        document.add(table);

        double totalEnergy = energyMap.values().stream().mapToDouble(Double::doubleValue).sum();

        Device highest = devices.stream()
                .max(Comparator.comparingDouble(d -> energyMap.get(d.getId())))
                .orElse(null);

        document.add(new Paragraph("\nSummary", boldFont));
        document.add(new Paragraph("Total Energy Today: " + String.format("%.3f kWh", totalEnergy)));
        document.add(new Paragraph("Estimated Cost: ₹" + String.format("%.2f", calculateCost(totalEnergy))));

        if (highest != null) {
            document.add(new Paragraph("Highest Consuming Device: " + highest.getName()));
        }

        document.close();
        out.close();
    }

    // 👤 USER-WISE ADMIN PDF REPORT
    public void generateUserEnergyReport(HttpServletResponse response,
                                         List<User> users) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=SHEMS-User-Energy-Report.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12);
        Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);

        Paragraph title = new Paragraph("User-wise Energy Usage Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph date = new Paragraph(
                "Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                normalFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(15);
        document.add(date);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        Stream.of("User Name", "Email", "Energy Today (kWh)")
                .forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Paragraph(header, boldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8);
                    table.addCell(cell);
                });

        double totalEnergy = 0;

        for (User u : users) {
            double energy = getTodayEnergyForUser(u.getId()); // NOW MATCHES Integer
            totalEnergy += energy;

            table.addCell(u.getName());
            table.addCell(u.getEmail());
            table.addCell(String.format("%.3f", energy));
        }

        document.add(table);

        document.add(new Paragraph("\nTotal System Energy Today: " +
                String.format("%.3f kWh", totalEnergy), boldFont));

        document.close();
    }
}
