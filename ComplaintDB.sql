-- =============================================
-- Complaint Monitoring System - Database
-- Microsoft SQL Server
-- =============================================

CREATE DATABASE ComplaintDB;
GO

USE ComplaintDB;
GO

-- Users Table
CREATE TABLE Users (
    UserID          INT IDENTITY(1,1) PRIMARY KEY,
    FullName        NVARCHAR(100) NOT NULL,
    Username        NVARCHAR(50)  NOT NULL UNIQUE,
    Password        NVARCHAR(100) NOT NULL,
    Email           NVARCHAR(100),
    Phone           NVARCHAR(20),
    Role            NVARCHAR(20)  NOT NULL DEFAULT 'User' CHECK (Role IN ('User','Admin')),
    CreatedAt       DATETIME DEFAULT GETDATE()
);

-- Complaints Table
CREATE TABLE Complaints (
    ComplaintID     INT IDENTITY(1,1) PRIMARY KEY,
    UserID          INT NOT NULL FOREIGN KEY REFERENCES Users(UserID),
    Subject         NVARCHAR(200) NOT NULL,
    Description     NVARCHAR(1000) NOT NULL,
    Status          NVARCHAR(30)  NOT NULL DEFAULT 'Pending' 
                    CHECK (Status IN ('Pending','In Progress','Resolved','Closed')),
    AdminReply      NVARCHAR(1000) NULL,
    CreatedAt       DATETIME DEFAULT GETDATE(),
    RepliedAt       DATETIME NULL
);

-- =============================================
-- Seed Data
-- =============================================

-- Default Admin
INSERT INTO Users (FullName, Username, Password, Email, Role)
VALUES ('System Admin', 'admin', 'admin123', 'admin@complaint.com', 'Admin');

-- Sample User
INSERT INTO Users (FullName, Username, Password, Email, Phone, Role)
VALUES ('Rahul Sharma', 'rahul', 'rahul123', 'rahul@email.com', '9876543210', 'User');

-- Sample Complaints
INSERT INTO Complaints (UserID, Subject, Description, Status)
VALUES 
(2, 'Internet not working', 'My broadband connection has been down since yesterday morning.', 'Pending'),
(2, 'Billing issue', 'I was charged twice for the month of August.', 'Pending');

PRINT 'ComplaintDB created successfully with seed data.';
GO
