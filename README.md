# TARUMT Facilities Booking System

A comprehensive facility booking solution for TARUMT, supporting multiple user roles with distinct permissions and capabilities.

---

## 📋 Table of Contents

- [Features](#features)
- [User Roles](#user-roles)
- [Data Structures & Algorithms](#data-structures--algorithms)
- [Technical Stack](#technical-stack)

---

## Features

### Core Capabilities

**Facility Management**
- Search, create, and update venue information
- Manage venue capacity and operational status (Active, Blocked, Maintenance)
- Real-time availability tracking across all facilities

**Booking System**
- Overlap-free booking with automatic conflict detection
- Intelligent waitlist management with automatic promotion
- Flexible booking status tracking (Active, Waitlisted, Cancelled, Completed, Forfeited)

**User Management**
- Multi-tier registration system with role-based access
- Admin approval workflow for new registrations
- User profile management and credential updates

**Reporting & Analytics**
- Venue utilization summaries
- Booking status breakdowns
- Peak time analysis
- User activity reports
- Historical action logs

---

## User Roles

### 👨‍💼 Administrator
- Full venue management (create, update, delete, search)
- Registration approval and user management
- Booking oversight with manual status override
- Waitlist monitoring and promotion
- Comprehensive system reporting

### 🎓 Normal Student
- Book available facility slots
- Join waitlists for occupied slots
- View venue availability across the system
- Manage personal bookings and booking history
- Cancel bookings (with automatic waitlist promotion)
- Update account credentials

### ⭐ Privileged User
- **Express booking** with override privileges on assigned venues
- Displace existing active bookings when necessary (displaced users auto-promoted to waitlist)
- Standard booking and profile management features
- View and manage personal bookings

### 🆕 New User / Guest
- Self-registration with student details
- Request Normal or Privileged role assignment
- Track registration status (Pending, Approved, Rejected, Suspended)
- Access to public venue information

---

## Data Structures & Algorithms

This system uses custom Abstract Data Types (ADTs) extending a **LinkedDeque** base, each implementing optimized sorting algorithms for specific use cases.

### ADT Overview

| ADT | Element Type | Sorting Algorithm | Complexity | Primary Use |
|-----|--------------|-------------------|-----------|-------------|
| **UserDQ** | User | Merge Sort | O(n log n) | Admin user list sorting (by ID, Name, Role, Status) |
| **VenueDQ** | Venue | Quick Sort | O(n log n) avg | Venue management and capacity reports |
| **BookingDQ** | Booking | Heap Sort | O(n log n) | Chronological booking history (ascending/descending) |
| **SorterDQ** | String[] | Merge Sort | O(n log n) | Report generation (active users, peak times) |

### Algorithm Rationale

**Merge Sort (UserDQ & SorterDQ)**
- Guarantees O(n log n) worst-case performance
- Maintains stability for consistent user record ordering
- Ideal for multi-criteria sorting in admin interfaces

**Quick Sort (VenueDQ)**
- Space-efficient in-place sorting
- Fast average-case performance for venue data
- Optimal for memory-constrained operations

**Heap Sort (BookingDQ)**
- Consistent O(n log n) performance
- In-place sorting without extra memory overhead
- Supports flexible ascending/descending chronological views

### Core Architecture

- **Foundation:** LinkedDeque<T> — doubly-linked list with O(1) head/tail operations
- **Validation:** Centralized ValidationUtils ensuring type safety, format correctness, and logical constraints
- **Design Pattern:** Inheritance-based specialization for domain-specific sorting behaviors

---

## Technical Stack

- **Language:** Java
- **Data Structure:** Custom LinkedDeque implementation with specialized extensions
- **Architecture:** Multi-tier role-based access control
- **Validation:** Comprehensive input validation and constraint checking

---

## Project Structure

```
TARUMT-Facilities-Booking-Services/
├── src/
│   ├── adt/                    # Custom ADT implementations
│   │   ├── LinkedDeque.java
│   │   ├── UserDQ.java
│   │   ├── VenueDQ.java
│   │   ├── BookingDQ.java
│   │   └── SorterDQ.java
│   ├── model/                  # Domain models
│   │   ├── User.java
│   │   ├── Venue.java
│   │   └── Booking.java
│   ├── service/                # Business logic
│   │   ├── BookingService.java
│   │   ├── VenueService.java
│   │   └── UserService.java
│   ├── utils/
│   │   └── ValidationUtils.java
│   └── ui/                     # User interfaces
│       ├── AdminPanel.java
│       ├── StudentPanel.java
│       └── RegistrationPanel.java
└── README.md
```

---

## Getting Started

1. Clone the repository
2. Compile all Java files in the `src/` directory
3. Run the main application entry point
4. Log in with your credentials or register a new account

---

## License

This project is developed as a BMCS2063 Data Structures & Algorithms assignment for TARUMT.