# Centralized Schedule and Reminder Management System

A Spring Boot web application for managing personal tasks, categories, and reminders with email/SMS delivery. Built with a clean monolithic architecture — authentication, data schema, scheduling infrastructure, and UI are in place, with core reminder dispatch in active development.

---

## What It Does

Users organize tasks into **categories** and set **reminders** with a scheduled delivery time and preferred channel (Email, SMS, or Both). A background scheduling job checks for due reminders and dispatches them automatically — no manual triggering required.

---

## Architecture

Monolithic Spring Boot application serving both the UI and backend logic from a single JVM process, backed by MySQL.

```
Browser (Thymeleaf/Bootstrap UI)
        │
        ▼
Spring MVC Controllers ──▶ Spring Security (login/auth)
        │
        ▼
  Service Layer (business logic + scheduling)
        │
        ▼
  Data layer (JPA/JDBC)
        │
        ▼
    MySQL database
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Web | Spring MVC |
| Auth | Spring Security (JdbcUserDetailsManager, form login) |
| Persistence | Spring Data JPA, Spring Data JDBC |
| Database | MySQL 8 |
| Templating | Thymeleaf + thymeleaf-extras-springsecurity6 |
| Email | Spring Boot Starter Mail (Gmail SMTP) |
| Frontend | Bootstrap 4.5.2, Font Awesome 5.15.4, vanilla CSS/JS |
| Scheduling | Spring `@Scheduled` tasks |
| Build | Maven (Maven Wrapper) |
| Testing | JUnit 5, Spring Boot Test, Spring Security Test |

---

## Features

**Authentication**
- Form-based login with Spring Security
- DB-backed user authentication via `JdbcUserDetailsManager`
- Role-based access control

**Task & Category Management**
- Create and organize tasks into user-specific categories
- Per-user category isolation enforced at the data layer

**Reminder Scheduling**
- Reminders stored with `reminder_datetime` and delivery channel (`Email`, `SMS`, or `Both`)
- Background scheduler polls for due reminders at a configurable fixed rate
- Email delivery via Gmail SMTP (Spring Mail)

**UI**
- Responsive dashboard with sections for Categories, Tasks, Reminders, and Account management
- Thymeleaf server-rendered views with Bootstrap styling

---

## Project Structure

```
src/main/java/com/schedulemanager/
├── config/
│   ├── SecurityConfig.java          # JdbcUserDetailsManager (DB-backed auth)
│   └── LoginPageSecurityConfig.java # HTTP security rules + form login
├── controller/
│   └── LoginController.java         # Login page, error/logout handling
src/main/resources/
├── application.yml                  # Server, datasource, mail, scheduling config
├── schema.sql                       # DB schema: categories, reminders
├── templates/                       # Thymeleaf views
└── static/                          # CSS/JS assets
```

---

## Running Locally

**Prerequisites:** Java 17+, MySQL 8, Maven

```bash
# 1. Create the database
mysql -u root -p -e "CREATE DATABASE local;"

# 2. Run the schema
mysql -u root -p local < src/main/resources/schema.sql

# 3. Configure credentials (never commit real values)
export DB_USERNAME=root
export DB_PASSWORD=your-mysql-password
export MAIL_USERNAME=you@gmail.com
export MAIL_PASSWORD=your-gmail-app-password

# 4. Start the app
./mvnw spring-boot:run
```

Visit `http://localhost:8081/loginpage`

---

## Roadmap

- [ ] JPA entities and repositories for categories and reminders
- [ ] REST/controller endpoints for dashboard CRUD operations
- [ ] `@Scheduled` job implementation for due-reminder dispatch
- [ ] JavaMailSender integration for live email delivery
- [ ] SMS delivery via Twilio (when `reminder_medium` = SMS or Both)
- [ ] Fix categories unique constraint (`user_id` column alignment)
- [ ] Wire login form fields to Spring Security expected field names

---

*Built by Mahathi Marepalli — [LinkedIn](https://www.linkedin.com/in/mahathi-marepalli/) · [Email](mailto:mahathimarepalli23@gmail.com)*
