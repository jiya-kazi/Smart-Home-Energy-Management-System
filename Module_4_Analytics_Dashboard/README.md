# Module 4 – Analytics Dashboard

## About Module 4
Module 4 is responsible for **Analytics and Visualization** in the Smart Home Energy Management System (SHEMS).  
This module focuses on **analyzing energy consumption data** collected from Module 3 and presenting it in a clear, structured, and meaningful way for both users and administrators.

It converts raw energy usage data into **insights, summaries, and trends** that support informed decision-making.

---

## Responsibilities of Module 4
- Analyze energy consumption data at device and user levels
- Generate daily, weekly, and monthly usage summaries
- Identify high energy consumption patterns
- Provide system-wide analytics for administrators
- Support energy optimization decisions

This module acts as the **decision-support and insight layer** of the system.

---

## Functional Features

### User Analytics
- View personal energy consumption summaries
- Monitor device-wise energy usage
- Identify peak usage periods
- Understand consumption trends over time

### Admin Analytics
- View system-wide energy consumption
- Identify top energy-consuming devices and users
- Analyze overall usage patterns
- Support policy and optimization decisions

---

## Project Structure

Module_4_Analytics_Dashboard/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/role/implementation/
│   │   │       ├── analytics/
│   │   │       │   ├── controller/
│   │   │       │   │   └── AnalyticsController.java
│   │   │       │   ├── service/
│   │   │       │   │   ├── AnalyticsService.java
│   │   │       │   │   └── AnalyticsServiceImpl.java
│   │   │       │   └── repository/
│   │   │       │       └── AnalyticsRepository.java
│   │   │       ├── model/
│   │   │       │   └── AnalyticsReport.java
│   │   │       └── RoleBasedAuthenticationApplication.java
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── analytics.html
│   │       │   ├── admin-analytics.html
│   │       │   ├── dashboard.html
│   │       │   └── dashboard-home.html
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│
├── pom.xml
├── mvnw
└── README.md

---

## Core Components

### AnalyticsController
Handles requests related to:
- User analytics views
- Admin analytics dashboards
- Energy summary reports

---

### AnalyticsService
Contains business logic for:
- Aggregating energy usage data
- Calculating usage trends
- Preparing analytical summaries

---

### AnalyticsRepository
Provides database access for retrieving and aggregating energy data using Spring Data JPA.

---

## User Interface
The following Thymeleaf templates are used:
- analytics.html – User analytics dashboard
- admin-analytics.html – Admin analytics dashboard
- dashboard.html – Integrated overview
- dashboard-home.html – User landing dashboard

---

## Technologies Used
- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven

---

## Execution

mvn spring-boot:run

Ensure database configuration is defined in application.properties.

---

## Summary
Module 4 transforms raw energy consumption data into **clear, actionable analytics**, enabling users to understand their usage patterns and administrators to monitor and optimize system-wide energy consumption.
