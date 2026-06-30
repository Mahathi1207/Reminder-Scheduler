# Centralized Schedule and Reminder Management System

A web application for managing personal tasks, categories, and reminders (with email/SMS delivery), built on Spring Boot. The project is in an early/scaffold stage: authentication, data schema, and configuration are in place, but several features described below (reminder dispatch, task/category APIs) are wired up via configuration and dependencies but **not yet implemented in code** — this is called out explicitly so the README stays accurate as the project grows.

## Architecture

**Monolithic** — a single Spring Boot application that serves both the server-rendered web UI (Thymeleaf) and (eventually) the backend logic, backed by one MySQL database. There is no service-to-service communication, message broker, or independent deployable services — everything runs in one JVM process.

```
Browser (Thymeleaf/Bootstrap UI)
        │
        ▼
Spring MVC Controllers  ──►  Spring Security (login/auth)
        │
        ▼
  Data layer (JPA/JDBC)
        │
        ▼
   MySQL database
```

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Web | Spring Web (Spring MVC) |
| Auth | Spring Security (`JdbcUserDetailsManager`, form login) |
| Persistence | Spring Data JPA, Spring Data JDBC |
| Database | MySQL 8 (`mysql-connector-java`) |
| Templating | Thymeleaf + `thymeleaf-extras-springsecurity6` |
| Email | Spring Boot Starter Mail (Gmail SMTP) |
| Frontend | Bootstrap 4.5.2, Font Awesome 5.15.4 (via CDN), vanilla CSS/JS |
| Build tool | Maven (with Maven Wrapper `mvnw` / `mvnw.cmd`) |
| Dev tooling | Spring Boot DevTools |
| Testing | JUnit 5 (Spring Boot Test), Spring Security Test |

## Project Structure

```
src/main/java/com/schedulemanager/
├── CentralizedScheduleAndReminderManagementSystemApplication.java   # Spring Boot entry point
├── config/
│   ├── SecurityConfig.java            # Registers JdbcUserDetailsManager (DB-backed auth)
│   └── LoginPageSecurityConfig.java   # HTTP security rules + form login setup
└── controller/
    └── LoginController.java           # Serves the login page, handles error/logout messages

src/main/resources/
├── application.yml      # Server, datasource, mail, and scheduling configuration
├── schema.sql           # DB schema: categories, reminders
├── templates/           # Thymeleaf views (login.html, home-page.html)
└── static/css, static/js  # Frontend assets
```

## Database Schema (`schema.sql`)

- **`categories`** — `category_id` (PK), `username` (FK → `users.username`), `category_name`, unique per user.
- **`reminders`** — `reminder_id` (PK), `category_id` (FK → `categories`), `reminder_datetime`, `reminder_medium` (`ENUM('SMS','Email','Both')`), `created_at`.

> **Note:** The `users` table referenced by the foreign keys is not defined in `schema.sql` — it's expected to come from Spring Security's JDBC auth schema (`users`/`authorities` tables), which must be created separately. Also, `categories` declares `UNIQUE (user_id, category_name)` but the table has no `user_id` column (only `username`) — this looks like a bug in the current schema and will need fixing before the categories table can be created successfully.

## Authentication / Login Flow

1. Unauthenticated requests to any page other than `/css/**`, `/js/**`, `/images/**`, and `/acceptRide/**` are redirected to `GET /loginpage` (`LoginPageSecurityConfig`).
2. `LoginController` renders the login form, showing an error message on bad credentials or a logout confirmation message, and redirects already-authenticated users straight to `/`.
3. Form submissions post to `/authenticateUser`, which Spring Security validates against the database via `JdbcUserDetailsManager` (`SecurityConfig`) — i.e., users/passwords/roles are expected to live in the database, not in-memory (an in-memory test user is present in code but commented out).
4. `login.html` is currently a static Bootstrap form — its inputs don't yet have `name="username"` / `name="password"` attributes wired to Spring Security's expected field names, so the login flow isn't fully functional yet.

## How Reminders Are Intended to Work

Based on the schema and configuration present, the design is:

1. A user creates a **category** (e.g., "Vehicle Maintenance") and **reminders** within it, each with a `reminder_datetime` and a delivery `reminder_medium` (Email, SMS, or Both) — modeled in `reminders` table.
2. A background job, running on the interval defined by `schedule.fixedRate` in `application.yml`, would periodically check for due reminders.
3. Due reminders would be dispatched via:
   - **Email** — using `spring-boot-starter-mail`, configured against Gmail SMTP (`smtp.gmail.com:587`) in `application.yml`.
   - **SMS** — no SMS provider/library (e.g., Twilio) is currently included as a dependency, so SMS delivery is not yet implemented.

**Current status:** the dependencies and config for this (mail starter, `schedule.fixedRate`) are present, but there is **no `@Scheduled` job, no `JavaMailSender` usage, no reminder/category JPA entities or repositories, and no REST/controller endpoints for creating or fetching reminders** anywhere in `src/main/java` yet. The dashboard (`home-page.html`) is a static HTML mockup of the intended UI (Dashboard, Categories, Tasks, Reminders, Account sections) with no JavaScript wiring to a backend (`homepage.js` is currently empty).

## Configuration (`application.yml`)

- Server runs on port **8081**.
- Datasource: MySQL at `jdbc:mysql://localhost:3306/local`.
- Mail: Gmail SMTP sender account.
- `schedule.fixedRate: 1000000` — intended interval (ms) for the future reminder-dispatch job.


## Running Locally

1. Have MySQL running locally and create a database named `local` (update credentials in `application.yml` if needed).
2. Run the Spring Security JDBC auth schema (`users`/`authorities` tables) against that database, then `src/main/resources/schema.sql` (after fixing the `categories` unique-constraint issue noted above).
3. Build and run:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Visit `http://localhost:8081/loginpage`.

## Known Gaps / TODO

- Fix `categories` table's `UNIQUE (user_id, category_name)` constraint (no `user_id` column exists).
- Define the `users` (and `authorities`) table(s) for Spring Security's `JdbcUserDetailsManager`.
- Wire `login.html` form fields to Spring Security's expected `username`/`password` field names.
- Implement JPA entities/repositories for `categories` and `reminders`.
- Implement REST/controller endpoints so the dashboard UI can actually create/list categories, tasks, and reminders.
- Implement the scheduled job (`@Scheduled`, driven by `schedule.fixedRate`) that finds due reminders and sends them.
- Implement actual email sending via `JavaMailSender`.
- Add an SMS provider integration if "SMS"/"Both" reminder mediums are to be supported.
- Remove hardcoded credentials from `application.yml` and externalize them.
- Remove the duplicate dependency entries in `pom.xml` (`spring-boot-starter-web` and `spring-boot-starter-test` are each declared twice).
