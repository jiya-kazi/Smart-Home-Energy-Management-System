# Module 2 – Smart Device Management

## About Module 2
Module 2 is responsible for **Smart Device Management** in the Smart Home Energy Management System (SHEMS).  
This module handles all operations related to **adding, controlling, and managing smart devices** used by users and administrators.

It ensures that devices are properly registered, controlled in real time, and prepared to generate data for energy tracking and further analysis.

---

## Responsibilities of Module 2
- Manage smart devices assigned to users
- Control device ON / OFF states
- Maintain device-related data for energy tracking
- Allow administrators to monitor and manage devices system-wide

This module acts as the **device control layer** of the system.

---

## Functional Features

### User Functions
- Add new smart devices
- View assigned devices
- Turn devices ON or OFF
- Remove devices when required

### Admin Functions
- View all devices across users
- Enable or disable any device
- Delete devices globally

---

## Project Structure

Module_2_Device_Management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/role/implementation/
│   │   │       ├── devicemanagement/
│   │   │       │   ├── controller/
│   │   │       │   │   └── DeviceController.java
│   │   │       │   ├── service/
│   │   │       │   │   ├── DeviceService.java
│   │   │       │   │   └── DeviceServiceImpl.java
│   │   │       │   └── repository/
│   │   │       │       └── DeviceRepository.java
│   │   │       ├── model/
│   │   │       │   └── Device.java
│   │   │       └── DTO/
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── devices.html
│   │       │   ├── adminDevices.html
│   │       │   └── admin-home.html
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

### Device Entity
Represents a smart device with attributes such as:
- Device name
- Device type
- Location
- Power rating
- Status (ON / OFF)
- Associated user

---

### DeviceController
Handles HTTP requests for:
- Adding devices
- Viewing device lists
- Toggling device status
- Deleting devices
- Admin-level device management

---

### DeviceService
Contains business logic for:
- Device ownership validation
- Device state control
- Preparing data for energy tracking

---

### DeviceRepository
Handles database operations related to smart devices using Spring Data JPA.

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
Module 2 provides **centralized and secure smart device management**, enabling real-time control and forming the foundation for energy tracking and system analytics in SHEMS.
