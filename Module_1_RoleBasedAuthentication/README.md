# Module 1 – Role Based Authentication

## About Module 1
Module 1 implements **Role Based Authentication and Authorization** for the Smart Home Energy Management System (SHEMS).  
This module is responsible for **user identity management, access control, and security**, ensuring that only authorized users can access specific system features.

It acts as the **foundation module** on which all other modules depend.

---

## Responsibilities of Module 1
- Authenticate users securely
- Authorize users based on roles
- Control access to system features
- Protect application endpoints
- Manage user credentials and sessions

This module ensures **secure entry and controlled access** to the system.

---

## Functional Features

### Authentication Features
- User registration
- User login and logout
- Password encryption
- Session management
- Access-denied handling

### Authorization Features
- Role-based access control (USER / ADMIN)
- Restricted access to admin functionalities
- Secure endpoint protection using roles

---

## Project Structure

Module_1_RoleBasedAuthentication/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/role/implementation/
│   │   │       ├── config/
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       │   └── AuthController.java
│   │   │       ├── service/
│   │   │       │   ├── UserService.java
│   │   │       │   └── UserServiceImpl.java
│   │   │       ├── repository/
│   │   │       │   └── UserRepository.java
│   │   │       ├── model/
│   │   │       │   ├── User.java
│   │   │       │   └── Role.java
│   │   │       └── RoleBasedAuthenticationApplication.java
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── login.html
│   │       │   ├── register.html
│   │       │   ├── access-denied.html
│   │       │   └── home.html
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

### User Entity
Represents application users with attributes:
- Username
- Email
- Password (encrypted)
- Role (USER / ADMIN)

---

### Security Configuration
Defines:
- Authentication mechanisms
- Role-based access rules
- Protected and public endpoints
- Login and logout behavior

---

### AuthController
Handles:
- Login requests
- Registration requests
- Access-denied routing
- Secure navigation control

---

### UserService
Contains business logic for:
- User registration
- Role assignment
- Password encoding
- User validation

---

### UserRepository
Handles database operations related to users and roles using Spring Data JPA.

---

## User Interface
Thymeleaf templates used:
- login.html – User login page
- register.html – User registration page
- access-denied.html – Unauthorized access page
- home.html – Landing page

---

## Technologies Used
- Java
- Spring Boot
- Spring Security
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
Module 1 provides **secure authentication and role-based authorization**, forming the backbone of access control for the entire Smart Home Energy Management System.
