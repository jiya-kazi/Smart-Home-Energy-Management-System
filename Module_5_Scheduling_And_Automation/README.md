# Module 5 – Scheduling and Automation

## About Module 5
Module 5 is responsible for **Scheduling and Automation** in the Smart Home Energy Management System (SHEMS).  
This module enables **automatic control of smart devices** based on predefined schedules and system rules, reducing manual effort and optimizing energy usage.

It ensures that devices operate at the right time without continuous user intervention.

---

## Responsibilities of Module 5
- Schedule device ON / OFF operations
- Automate device control based on time rules
- Enforce system-level automation policies
- Support energy-efficient device operation

This module acts as the **automation and control layer** of the system.

---

## Functional Features

### Scheduling Features
- Create device schedules
- Define ON time and OFF time for devices
- Enable or disable schedules
- Automatically execute schedules at runtime

### Automation Features
- Automatic device control without manual input
- Centralized enforcement of automation rules
- Logging of automated actions for tracking

---

## Project Structure

Module_5_Scheduling_and_Automation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/role/implementation/
│   │   │       ├── automation/
│   │   │       │   ├── controller/
│   │   │       │   │   └── ScheduleController.java
│   │   │       │   ├── service/
│   │   │       │   │   ├── AutomationService.java
│   │   │       │   │   └── AutomationServiceImpl.java
│   │   │       │   └── repository/
│   │   │       │       └── ScheduleRepository.java
│   │   │       ├── model/
│   │   │       │   └── Schedule.java
│   │   │       └── RoleBasedAuthenticationApplication.java
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── schedules.html
│   │       │   ├── automation.html
│   │       │   └── policy-enforcement-logs.html
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

### Schedule Entity
Represents scheduling information with attributes such as:
- Device reference
- ON time
- OFF time
- Schedule status (enabled / disabled)

---

### ScheduleController
Handles requests related to:
- Creating schedules
- Viewing schedules
- Enabling or disabling schedules
- Executing automation actions

---

### AutomationService
Contains business logic for:
- Evaluating schedules
- Triggering device ON / OFF operations
- Managing automation execution

---

### ScheduleRepository
Handles database operations for storing and retrieving scheduling data using Spring Data JPA.

---

## User Interface
The following Thymeleaf templates are used:
- schedules.html – Device scheduling interface
- automation.html – Automation overview
- policy-enforcement-logs.html – Automation logs

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
Module 5 enables **automated and scheduled control of smart devices**, reducing manual intervention and supporting efficient, rule-based energy management within the SHEMS platform.
