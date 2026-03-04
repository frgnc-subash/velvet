# Hall Booking Management System
## Supporting Document: System Design and Class Structure

## 1. Introduction
This document describes the overall project structure, class design, and object-oriented architecture for the Hall Booking Management System.

The system is implemented in Java using Swing GUI and follows object-oriented programming principles:
- Abstraction
- Inheritance
- Encapsulation
- Polymorphism

Data persistence uses text files only, aligned with the coursework rule (no database engines).

## 2. Project Architecture
The project follows layered architecture:
- Presentation Layer (GUI)
- Controller Layer
- Service Layer (Business Logic)
- Model Layer (Entities)
- Utility and File Handling Layer

This structure separates concerns and improves maintainability.

## 3. Maven Project Folder Structure
```text
velvet/
├── pom.xml
├── README.md
├── SUPPORTING_DOCUMENT.md
└── src/
    └── main/
        ├── java/
        │   └── org/velvet/
        │       ├── Main.java
        │       ├── AppContext.java
        │       ├── exception/
        │       ├── gui/
        │       ├── model/
        │       │   ├── booking/
        │       │   ├── controller/
        │       │   ├── hall/
        │       │   ├── issue/
        │       │   ├── payment/
        │       │   ├── service/
        │       │   └── user/
        │       └── util/
        └── resources/
            └── data/
                ├── users.txt
                ├── halls.txt
                ├── bookings.txt
                ├── payments.txt
                ├── maintenance.txt
                └── issues.txt
```

## 4. Model Layer Class Design
### 4.1 User Hierarchy
`User` (abstract)
- Attributes: `id`, `name`, `username`, `password`, `role`, `phone`, `email`, `blocked`
- Common behavior: serialization and identity/profile operations

`Customer` extends `User`
- Actions used by controller/service: register, update profile, booking, payment, issue raising

`Staff` (abstract) extends `User`
- Specialized by role-based staff classes

`Scheduler`, `Administrator`, `Manager` extend `Staff`
- Scheduler: hall and maintenance operations
- Administrator: scheduler/user management and booking overview
- Manager: sales and issue operations

### 4.2 Hall Hierarchy
`Hall` (abstract)
- Attributes: `id`, `name`, `type`, `capacity`, `ratePerHour`, `status`, `availableFrom`, `availableTo`, `remarks`
- Methods: `calculateCost()`, `updateStatus()`, `getHallDetails()`

Concrete halls:
- `Auditorium` (1000 seats, RM 300/hr)
- `BanquetHall` (300 seats, RM 100/hr)
- `MeetingRoom` (30 seats, RM 50/hr)

### 4.3 Booking and Scheduling
`Booking`
- Attributes: `id`, `customerId`, `customerName`, `hallId`, `hallName`, `hallType`, `start`, `end`, `totalAmount`, `status`, `createdAt`
- Methods: `calculateDuration()`, `calculateTotalAmount()`, `confirmBooking()`, `cancelBooking()`, `isCancellable()`

`BookingStatus` (enum)
- `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`

`TimeSlot`
- Attributes: `start`, `end`, `remarks`
- Methods: `overlaps()`, `isValidSlot()`

### 4.4 Payment
`Payment`
- Attributes: `id`, `bookingId`, `customerId`, `amount`, `method`, `status`, `paidAt`

`Receipt`
- Attribute: receipt text output for booking/payment detail display in GUI

### 4.5 Issue and Maintenance
`Issue`
- Attributes: `id`, `bookingId`, `customerId`, `hallId`, `description`, `status`, `assignedSchedulerId`, `managerResponse`, `createdAt`, `updatedAt`

`IssueStatus` (enum)
- `IN_PROGRESS`, `DONE`, `CLOSED`, `CANCELLED`

`MaintenanceTask`
- Attributes: `id`, `hallId`, `hallName`, `start`, `end`, `remarks`, `schedulerId`
- Method: `overlaps()` to protect booking windows

## 5. Service Layer
- `UserService`: login, registration, profile update, role filtering, block/delete user
- `HallService`: hall CRUD, availability management, maintenance task CRUD
- `BookingService`: booking creation, conflict checks, cancellation rule (>=3 days), admin/customer filtering
- `PaymentService`: payment processing and receipt generation
- `IssueService`: issue creation, scheduler assignment, issue status updates
- `ReportService`: weekly/monthly/yearly sales aggregation

## 6. Utility and File Handling
- `FileHandler`: read/write/append/update/delete record support for `.txt` persistence
- `IdGenerator`: prefixed timestamp-random ID generation
- `ValidationUtil`: email/phone/password/date-range validation
- `DateTimeUtil`: date formatting/parsing, business-hour checks, date arithmetic helpers

## 7. Controller Layer
- `LoginController`: login + customer registration
- `CustomerController`: profile, booking, payment, receipt, cancellation, issue submission
- `SchedulerController`: hall management + maintenance management
- `AdminController`: scheduler/user management + booking overview
- `ManagerController`: sales reporting + issue workflow

## 8. GUI Layer (Swing)
- Login: `LoginFrame`
- Customer: `CustomerDashboard`, `BookingFrame`, `PaymentFrame`, `ReceiptFrame`, `IssueFrame`
- Scheduler: `SchedulerDashboard`, `HallManagementFrame`, `MaintenanceFrame`
- Administrator: `AdminDashboard`, `UserManagementFrame`, `BookingOverviewFrame`
- Manager: `ManagerDashboard`, `SalesDashboardFrame`, `IssueManagementFrame`

## 9. Conclusion
The project implements a complete object-oriented hall booking system with file-based persistence and role-based GUI workflows required by the coursework.

The design demonstrates:
- Clear abstraction of domain entities
- Inheritance hierarchy for users and halls
- Encapsulated business rules in services
- Controller-mediated GUI operations
- Text-file storage compliant with assignment constraints
