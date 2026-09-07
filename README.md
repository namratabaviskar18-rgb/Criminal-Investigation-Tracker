# Complaint Monitoring System

Android + ASP.NET Core Web API + SQL Server

Based on the project presentation.

## Features

### User (Android App)
- Register
- Login
- Post Complaint
- View own Complaints + Admin Replies

### Admin (via same API / can be extended)
- View all complaints
- Reply to complaints (reply is sent back to the Android user)

## Project Structure

```
ComplaintMonitoringSystem/
├── Database/
│   └── ComplaintDB.sql          ← Run this first in SQL Server
├── Backend/                     ← ASP.NET Core Web API
│   ├── Controllers/
│   ├── Models/
│   ├── Data/
│   └── Program.cs
└── AndroidApp/                  ← Android Studio Project files
    └── app/src/main/
        ├── java/com/complaintmonitor/
        ├── res/layout/
        └── AndroidManifest.xml
```

## How to Run

### 1. Database
1. Open SQL Server Management Studio
2. Run `Database/ComplaintDB.sql`

### 2. Backend (API Server)
1. Open Visual Studio
2. Create new **ASP.NET Core Web API** project
3. Copy the files from `Backend/` folder
4. Install package: `System.Data.SqlClient` (or Microsoft.Data.SqlClient)
5. Update connection string in `DatabaseHelper.cs` if needed
6. Run the project (default: https://localhost:7xxx or http://localhost:5xxx)

**Important for Android Emulator:**  
In `ApiClient.java` keep:
```java
public static String BASE_URL = "http://10.0.2.2:5000/api/";
```
(Use the port shown when you run the API)

For real phone → change to your PC’s IP address (e.g. `http://192.168.1.5:5000/api/`)

### 3. Android App
1. Open **Android Studio**
2. Create new project → Empty Activity → Package name: `com.complaintmonitor`
3. Copy all Java files into `app/src/main/java/com/complaintmonitor/`
4. Copy all XML layouts into `app/src/main/res/layout/`
5. Replace `AndroidManifest.xml`
6. Make sure internet permission is present
7. Run on Emulator or real device

## Default Login Credentials

| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | admin    | admin123  |
| User  | rahul    | rahul123  |

## API Endpoints

| Method | Endpoint                  | Description                |
|--------|---------------------------|----------------------------|
| POST   | /api/auth/register        | User registration          |
| POST   | /api/auth/login           | Login                      |
| POST   | /api/complaint/post       | Post new complaint         |
| GET    | /api/complaint/my/{userId}| Get user’s complaints      |
| GET    | /api/complaint/all        | Get all complaints (Admin) |
| POST   | /api/complaint/reply      | Admin reply to complaint   |

## Notes
- This is a student-level project.
- Passwords are stored in plain text (for simplicity).
- You can add an Admin web page later using the same API.
