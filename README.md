# MediVault — Patient Management System

Minimal Java Swing desktop app with 3-layer architecture,
custom exception handling, file-based logging, and MySQL integration.

---

## Project Structure

```
MediVault/
├── src/com/medivault/
│   ├── model/
│   │   └── Patient.java                ← POJO / data model
│   ├── db/
│   │   └── DBConnection.java           ← JDBC MySQL connection
│   ├── dao/
│   │   └── PatientDAO.java             ← SQL: INSERT + SELECT
│   ├── service/
│   │   └── PatientService.java         ← Input validation + business logic
│   ├── util/
│   │   └── LogUtil.java                ← FILE HANDLING: writes to log.txt
│   ├── exception/
│   │   └── InvalidInputException.java  ← Custom exception
│   └── ui/
│       ├── LoginUI.java                ← Login screen + main()
│       ├── DashboardUI.java            ← Main menu
│       ├── AddPatientUI.java           ← Add patient form
│       └── ViewPatientUI.java          ← Patient list (JTable)
├── setup.sql                           ← MySQL setup script
└── README.md
```

---

## Prerequisites

| Requirement       | Notes                                           |
|-------------------|-------------------------------------------------|
| Java JDK 8+       | Any modern JDK works                            |
| MySQL             | Running on localhost:3306                       |
| MySQL Connector/J | `mysql-connector-j-8.x.x.jar` on classpath     |

Download Connector/J: https://dev.mysql.com/downloads/connector/j/

---

## Quick Start

### 1 — Run setup.sql
```bash
mysql -u root -p < setup.sql
```

### 2 — Edit DB credentials in DBConnection.java
```java
private static final String USER     = "root";
private static final String PASSWORD = "your_password";
```

### 3 — Compile (from the MediVault/ directory)
```bash
# Windows
javac -cp ".;mysql-connector-j-8.x.x.jar" -d out -sourcepath src src/com/medivault/ui/LoginUI.java

# macOS / Linux
javac -cp ".:mysql-connector-j-8.x.x.jar" -d out -sourcepath src src/com/medivault/ui/LoginUI.java
```

### 4 — Run
```bash
# Windows
java -cp ".;out;mysql-connector-j-8.x.x.jar" com.medivault.ui.LoginUI

# macOS / Linux
java -cp ".:out:mysql-connector-j-8.x.x.jar" com.medivault.ui.LoginUI
```

---

## Login Credentials

| Field    | Value   |
|----------|---------|
| Username | `admin` |
| Password | `123`   |

---

## Exception Handling Map

| Class              | Exception Type           | Scenario                              |
|--------------------|--------------------------|---------------------------------------|
| DBConnection       | ClassNotFoundException   | Connector JAR not on classpath        |
| DBConnection       | SQLException             | DB offline / wrong credentials        |
| PatientDAO         | SQLException             | INSERT or SELECT fails                |
| PatientService     | InvalidInputException    | Blank name/phone, bad age range       |
| AddPatientUI       | NumberFormatException    | Non-numeric text in Age field         |
| AddPatientUI       | InvalidInputException    | Caught from PatientService            |
| AddPatientUI       | Exception (generic)      | Unexpected runtime error              |
| ViewPatientUI      | Exception (generic)      | Data load failure                     |
| LoginUI            | Exception (generic)      | Unexpected login error                |
| LogUtil            | IOException              | Silent fallback if log.txt fails      |

All user-visible exceptions are shown with **JOptionPane** — never raw stack traces.

---

## File Handling — log.txt

- Written by `LogUtil.java` using `BufferedWriter + FileWriter(file, true)` (append mode).
- Created automatically in the working directory on first log entry.

### Sample output
```
[2024-06-01 10:00:01] MediVault application started.
[2024-06-01 10:00:08] Login successful — user: admin
[2024-06-01 10:00:15] Opened: Add Patient screen.
[2024-06-01 10:00:28] Invalid input: age is not a number — 'abc'
[2024-06-01 10:00:45] Patient added successfully: name=Alice, age=30, phone=9876543210
[2024-06-01 10:01:00] Fetched 3 patient(s) from database.
[2024-06-01 10:01:10] Application exited by user.
```

---

## Architecture

```
  ┌─────────────────────────────────────────┐
  │          UI Layer (Swing)               │
  │  LoginUI → DashboardUI                  │
  │  AddPatientUI  ViewPatientUI            │
  └──────────────┬──────────────────────────┘
                 │ calls
  ┌──────────────▼──────────────────────────┐
  │         Service Layer                   │
  │  PatientService  (validates input,      │
  │  throws InvalidInputException)          │
  └──────────────┬──────────────────────────┘
                 │ calls
  ┌──────────────▼──────────────────────────┐
  │           DAO Layer                     │
  │  PatientDAO  (SQL via PreparedStatement) │
  │  DBConnection (JDBC Connection)          │
  └──────────────┬──────────────────────────┘
                 │
  ┌──────────────▼──────────────────────────┐
  │            MySQL Database               │
  │  medivault.patients                     │
  └─────────────────────────────────────────┘

  All layers ──► LogUtil ──► log.txt   (file handling)
```
