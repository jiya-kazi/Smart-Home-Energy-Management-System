package com.role.implementation.energytracking.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.model.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

@Service
public class PdfReportService {

    // =========================================================
    // 📟 DEVICE-WISE ENERGY REPORT
    // =========================================================
    public void generateDeviceEnergyReport(HttpServletResponse response,
                                           List<Device> devices,
                                           Map<Long, Double> deviceEnergyMap,
                                           Map<Long, String> usageLevelMap) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=device-energy-report.pdf");

        Document document = new Document(PageSize.A4, 50, 50, 55, 50);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Font dateFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Font bodyFont = new Font(Font.HELVETICA, 11);
        Font summaryFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);

        Paragraph title = new Paragraph("Device Energy Usage Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph date = new Paragraph("Generated on: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")), dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(14);
        document.add(date);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.6f, 2f, 1.6f, 2f, 1.4f});
        table.setSpacingAfter(12);

        String[] headers = {"Device Name", "Type", "Power", "Energy Today (kWh)", "Usage Level"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(37, 99, 235));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(7);
            cell.setBorderColor(new Color(200, 200, 200));
            table.addCell(cell);
        }

        double totalEnergy = 0;

        for (Device d : devices) {
            double energy = deviceEnergyMap.getOrDefault(d.getId(), 0.0);
            totalEnergy += energy;

            table.addCell(createBodyCell(d.getName(), bodyFont));
            table.addCell(createBodyCell(d.getType(), bodyFont));
            table.addCell(createBodyCell(String.format("%.0f W", d.getPowerRating()), bodyFont));
            table.addCell(createBodyCell(String.format("%.3f", energy), bodyFont));

            // Usage Level with Color
            String level = usageLevelMap.get(d.getId());
            PdfPCell usageCell = new PdfPCell(new Phrase(level, bodyFont));
            usageCell.setPadding(6);

            if ("HIGH".equalsIgnoreCase(level)) {
                usageCell.setBackgroundColor(new Color(255, 199, 206));
            } else if ("MEDIUM".equalsIgnoreCase(level)) {
                usageCell.setBackgroundColor(new Color(255, 235, 156));
            } else {
                usageCell.setBackgroundColor(new Color(198, 239, 206));
            }

            table.addCell(usageCell);
        }

        document.add(table);

        Paragraph chartHeading = new Paragraph("Energy Usage per Device", summaryFont);
        chartHeading.setSpacingAfter(6);
        document.add(chartHeading);

        Image chart = generateBarChart(devices, deviceEnergyMap);
        chart.scaleToFit(440, 250);
        chart.setAlignment(Element.ALIGN_CENTER);
        document.add(chart);

        Paragraph summary = new Paragraph(
                "Total Energy Consumption Today: " + String.format("%.3f", totalEnergy) + " kWh",
                summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        Paragraph footer = new Paragraph("SHEMS – Smart Home Energy Management System", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(22);
        document.add(footer);

        document.close();
    }

    private PdfPCell createBodyCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setBorderColor(new Color(220, 220, 220));
        return cell;
    }

    private Image generateBarChart(List<Device> devices, Map<Long, Double> deviceEnergyMap)
            throws IOException, BadElementException {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Device d : devices) {
            dataset.addValue(deviceEnergyMap.getOrDefault(d.getId(), 0.0), "Energy", d.getName());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "", "Device", "Energy (kWh)",
                dataset, PlotOrientation.VERTICAL, false, false, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(37, 99, 235));
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);

        BufferedImage image = chart.createBufferedImage(600, 350);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(image, "png", baos);

        return Image.getInstance(baos.toByteArray());
    }

    // =========================================================
    // 👤 USER-WISE ENERGY REPORT
    // =========================================================
    public void generateUserEnergyReport(HttpServletResponse response,
                                         List<User> users,
                                         Map<Integer, Double> userEnergyMap,
                                         Map<Integer, Double> userCostMap) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=user-energy-report.pdf");

        Document document = new Document(PageSize.A4, 50, 50, 55, 50);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Font dateFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Font bodyFont = new Font(Font.HELVETICA, 11);
        Font summaryFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);

        Paragraph title = new Paragraph("User-wise Energy Usage Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph date = new Paragraph("Generated on: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")), dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(14);
        document.add(date);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 3f, 2f, 2f});
        table.setSpacingAfter(12);

        String[] headers = {"User Name", "Email", "Energy Today (kWh)", "Cost (₹)"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(16, 163, 74));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(7);
            table.addCell(cell);
        }

        double totalEnergy = 0;
        double totalCost = 0;

        for (User user : users) {
            double energy = userEnergyMap.getOrDefault(user.getId(), 0.0);
            double cost = userCostMap.getOrDefault(user.getId(), 0.0);

            totalEnergy += energy;
            totalCost += cost;

            table.addCell(createBodyCell(user.getName(), bodyFont));
            table.addCell(createBodyCell(user.getEmail(), bodyFont));
            table.addCell(createBodyCell(String.format("%.3f", energy), bodyFont));
            table.addCell(createBodyCell(String.format("%.2f", cost), bodyFont));
        }

        document.add(table);

        Paragraph summary = new Paragraph(
                "Total System Energy Today: " + String.format("%.3f", totalEnergy) + " kWh\n" +
                        "Estimated Total Cost: ₹" + String.format("%.2f", totalCost),
                summaryFont);
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        Paragraph footer = new Paragraph("SHEMS – Smart Home Energy Management System", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(22);
        document.add(footer);

        document.close();
    }
}




//package com.role.implementation.energytracking.service;
//
//import com.lowagie.text.*;
//import com.lowagie.text.pdf.*;
//import com.role.implementation.devicemanagement.model.Device;
//import com.role.implementation.model.User;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.http.HttpServletResponse;
//import java.awt.Color;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Map;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.renderer.category.BarRenderer;
//import org.jfree.chart.renderer.category.StandardBarPainter;
//import org.jfree.data.category.DefaultCategoryDataset;
//
//@Service
//public class PdfReportService {
//
//    // =========================================================
//    // 📟 DEVICE-WISE ENERGY REPORT
//    // =========================================================
//    public void generateDeviceEnergyReport(HttpServletResponse response,
//                                           List<Device> devices,
//                                           Map<Long, Double> deviceEnergyMap,
//                                           Map<Long, String> usageLevelMap) throws IOException {
//
//        // ⭐⭐ FIX: Tell browser this is a PDF download
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=device-energy-report.pdf");
//
//        Document document = new Document(PageSize.A4, 50, 50, 55, 50);
//        PdfWriter.getInstance(document, response.getOutputStream());
//        document.open();
//
//        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
//        Font dateFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
//        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
//        Font bodyFont = new Font(Font.HELVETICA, 11);
//        Font summaryFont = new Font(Font.HELVETICA, 12, Font.BOLD);
//        Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
//
//        Paragraph title = new Paragraph("Device Energy Usage Report", titleFont);
//        title.setAlignment(Element.ALIGN_CENTER);
//        title.setSpacingAfter(4);
//        document.add(title);
//
//        Paragraph date = new Paragraph("Generated on: " +
//                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")), dateFont);
//        date.setAlignment(Element.ALIGN_CENTER);
//        date.setSpacingAfter(14);
//        document.add(date);
//
//        PdfPTable table = new PdfPTable(5);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{2.6f, 2f, 1.6f, 2f, 1.4f});
//        table.setSpacingAfter(12);
//
//        String[] headers = {"Device Name", "Type", "Power (W)", "Energy Today (kWh)", "Usage Level"};
//
//        for (String header : headers) {
//            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
//            cell.setBackgroundColor(new Color(37, 99, 235));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(7);
//            table.addCell(cell);
//        }
//
//        double totalEnergy = 0;
//
//        for (Device d : devices) {
//            double energy = deviceEnergyMap.getOrDefault(d.getId(), 0.0);
//            totalEnergy += energy;
//
//            table.addCell(createBodyCell(d.getName(), bodyFont));
//            table.addCell(createBodyCell(d.getType(), bodyFont));
//            table.addCell(createBodyCell(String.valueOf(d.getPowerRating()), bodyFont));
//            table.addCell(createBodyCell(String.format("%.3f", energy), bodyFont));
//            table.addCell(createBodyCell(usageLevelMap.get(d.getId()), bodyFont));
//        }
//
//        document.add(table);
//
//        Paragraph chartHeading = new Paragraph("Energy Usage per Device", summaryFont);
//        chartHeading.setSpacingAfter(6);
//        document.add(chartHeading);
//
//        Image chart = generateBarChart(devices, deviceEnergyMap);
//        chart.scaleToFit(440, 250);
//        chart.setAlignment(Element.ALIGN_CENTER);
//        document.add(chart);
//
//        Paragraph summary = new Paragraph(
//                "Total Energy Consumption Today: " + String.format("%.3f", totalEnergy) + " kWh",
//                summaryFont);
//        summary.setAlignment(Element.ALIGN_RIGHT);
//        summary.setSpacingBefore(6);
//        document.add(summary);
//
//        Paragraph footer = new Paragraph("SHEMS – Smart Home Energy Management System", footerFont);
//        footer.setAlignment(Element.ALIGN_CENTER);
//        footer.setSpacingBefore(22);
//        document.add(footer);
//
//        document.close();
//    }
//
//    private PdfPCell createBodyCell(String text, Font font) {
//        PdfPCell cell = new PdfPCell(new Phrase(text, font));
//        cell.setPadding(6);
//        return cell;
//    }
//
//    private Image generateBarChart(List<Device> devices, Map<Long, Double> deviceEnergyMap)
//            throws IOException, BadElementException {
//
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        for (Device d : devices) {
//            dataset.addValue(deviceEnergyMap.getOrDefault(d.getId(), 0.0), "Energy", d.getName());
//        }
//
//        JFreeChart chart = ChartFactory.createBarChart(
//                "", "Device", "Energy (kWh)",
//                dataset, PlotOrientation.VERTICAL, false, false, false);
//
//        CategoryPlot plot = chart.getCategoryPlot();
//        plot.setBackgroundPaint(Color.WHITE);
//
//        BarRenderer renderer = (BarRenderer) plot.getRenderer();
//        renderer.setSeriesPaint(0, new Color(37, 99, 235));
//        renderer.setBarPainter(new StandardBarPainter());
//        renderer.setShadowVisible(false);
//
//        BufferedImage image = chart.createBufferedImage(600, 350);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        javax.imageio.ImageIO.write(image, "png", baos);
//
//        return Image.getInstance(baos.toByteArray());
//    }
//
//    // =========================================================
//    // 👤 USER-WISE ENERGY REPORT
//    // =========================================================
//    public void generateUserEnergyReport(HttpServletResponse response,
//                                         List<User> users,
//                                         Map<Integer, Double> userEnergyMap,
//                                         Map<Integer, Double> userCostMap) throws IOException {
//
//        // ⭐⭐ FIX HERE ALSO
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=user-energy-report.pdf");
//
//        Document document = new Document(PageSize.A4, 50, 50, 55, 50);
//        PdfWriter.getInstance(document, response.getOutputStream());
//        document.open();
//
//        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
//        Font dateFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
//        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
//        Font bodyFont = new Font(Font.HELVETICA, 11);
//        Font summaryFont = new Font(Font.HELVETICA, 12, Font.BOLD);
//        Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
//
//        Paragraph title = new Paragraph("User-wise Energy Usage Report", titleFont);
//        title.setAlignment(Element.ALIGN_CENTER);
//        document.add(title);
//
//        Paragraph date = new Paragraph("Generated on: " +
//                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")), dateFont);
//        date.setAlignment(Element.ALIGN_CENTER);
//        date.setSpacingAfter(14);
//        document.add(date);
//
//        PdfPTable table = new PdfPTable(4);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{2.5f, 3f, 2f, 2f});
//        table.setSpacingAfter(12);
//
//        String[] headers = {"User Name", "Email", "Energy Today (kWh)", "Cost (₹)"};
//
//        for (String header : headers) {
//            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
//            cell.setBackgroundColor(new Color(16, 163, 74));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(7);
//            table.addCell(cell);
//        }
//
//        double totalEnergy = 0;
//        double totalCost = 0;
//
//        for (User user : users) {
//            double energy = userEnergyMap.getOrDefault(user.getId(), 0.0);
//            double cost = userCostMap.getOrDefault(user.getId(), 0.0);
//
//            totalEnergy += energy;
//            totalCost += cost;
//
//            table.addCell(createBodyCell(user.getName(), bodyFont));
//            table.addCell(createBodyCell(user.getEmail(), bodyFont));
//            table.addCell(createBodyCell(String.format("%.3f", energy), bodyFont));
//            table.addCell(createBodyCell(String.format("%.2f", cost), bodyFont));
//        }
//
//        document.add(table);
//
//        Paragraph summary = new Paragraph(
//                "Total System Energy Today: " + String.format("%.3f", totalEnergy) + " kWh\n" +
//                        "Estimated Total Cost: ₹" + String.format("%.2f", totalCost),
//                summaryFont);
//        summary.setAlignment(Element.ALIGN_RIGHT);
//        document.add(summary);
//
//        Paragraph footer = new Paragraph("SHEMS – Smart Home Energy Management System", footerFont);
//        footer.setAlignment(Element.ALIGN_CENTER);
//        footer.setSpacingBefore(22);
//        document.add(footer);
//
//        document.close();
//    }
//}
