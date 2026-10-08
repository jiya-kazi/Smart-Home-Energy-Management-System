# Smart Home Energy Management System (SHEMS)

The Smart Home Energy Management System (SHEMS) is a role-based web application developed using Spring Boot that enables monitoring, control, and optimization of household energy consumption. The system provides real-time energy tracking, analytics, automated device scheduling, and administrative energy policy enforcement.

This project follows clean architecture principles, modular design, and secure authentication practices to simulate a real-world smart energy management platform.

---

## Table of Contents

1. Project Overview  
2. Features  
3. System Architecture  
4. Modules  
5. Technology Stack  
6. Database Design  
7. Installation and Setup  
8. Usage  
9. Roles and Permissions  
10. Project Highlights  
11. License  

---

## Project Overview

SHEMS is a full-stack Java web application designed to improve energy efficiency in smart homes. It allows users to monitor device energy usage, control appliances, and automate schedules. Administrators can analyze system-wide energy trends, enforce consumption policies, and generate reports.

The application uses Spring Boot for backend services, Spring Security for authentication and authorization, and MySQL for persistent storage.

---

## Features

### User Features
- Secure registration and login  
- Smart device control (ON/OFF)  
- Real-time energy usage tracking  
- Personal energy analytics dashboard  
- Automated device scheduling  
- Energy-saving recommendations  

### Administrator Features
- System-wide energy monitoring  
- High-usage user identification  
- Top energy-consuming device analysis  
- Energy policy creation and enforcement  
- PDF report generation  
- User and device management  

---

## System Architecture

The system is built using a layered architecture:

- Presentation Layer (Thymeleaf, HTML, CSS)  
- Controller Layer (Spring MVC Controllers)  
- Service Layer (Business Logic)  
- Repository Layer (Spring Data JPA)  
- Database Layer (MySQL)

This structure ensures separation of concerns, maintainability, and scalability.

---

## Modules

### Module 1: Role-Based Authentication
Provides secure login, registration, role management, password encryption, and email-based password reset functionality.

### Module 2: Smart Device Management
Allows users to add, update, delete, and control smart devices with attributes such as type, location, and power rating.

### Module 3: Real-Time Energy Tracking
Tracks device-level and user-level energy consumption, calculates electricity cost, and detects peak usage.

### Module 4: Analytics Dashboard
Displays energy usage trends, comparisons, peak detection, and intelligent recommendations for both users and administrators.

### Module 5: Scheduling and Automation
Implements automated device control using time-based schedules and enforces energy consumption policies through background schedulers.

---

## Technology Stack

Backend developed using Spring Boot.  
Security implemented using Spring Security and BCrypt.  
Persistence handled with Spring Data JPA (Hibernate).  
Database used is MySQL.  
Frontend built with Thymeleaf, HTML, and CSS.  
Task scheduling handled using Spring Scheduler.  
PDF reporting implemented using iText.  
Project build and dependency management handled using Maven.  
Compatible with Java 17 and above.

---

## Database Design

The database follows a normalized relational structure:

- One User can own multiple Devices  
- One Device can have multiple Energy Usage records  
- One Device can have multiple Schedules  
- One User has one Password Reset Token  
- One Energy Policy can produce multiple Enforcement Logs  

Referential integrity is enforced using foreign key constraints.

---

## Installation and Setup

### Prerequisites

- Java 17 or higher  
- Maven  
- MySQL  
- IDE such as IntelliJ IDEA, Eclipse, or VS Code  

### Steps

Clone the repository:

```bash
git clone https://github.com/fahimshaik36/shems-team3.git
cd shems-team3
```

Configure database credentials in:

```
src/main/resources/application.properties
```

Run the application:

```bash
mvn spring-boot:run
```

Access the application in a browser:

```
http://localhost:8080
```

---

## Usage

1. Register as a new user or log in with existing credentials.  
2. Add smart devices and control their status.  
3. Monitor daily energy usage and analytics.  
4. Configure device schedules for automation.  
5. Administrators can log in to access system-wide analytics and policy management.

---

## Roles and Permissions

### User
- Manage personal devices  
- View individual energy usage  
- Access analytics dashboard  
- Schedule device automation  

### Administrator
- Manage all users and devices  
- View system-wide analytics  
- Enforce energy consumption policies  
- Generate PDF energy reports  

---

## Project Highlights

- Real-world Spring Boot application architecture  
- Secure role-based authentication and authorization  
- Intelligent automation with scheduled background tasks  
- Data-driven energy optimization  
- Policy-based administrative control  
- Transaction-safe database operations  
- Modular, scalable design

---

## License

This project is developed for academic and learning purposes under the MIT License.
