# TARUMT-Facilities-Booking-Services
Facility Booking System for DSA Assignment

## Features Overview

This system provides a comprehensive facility booking solution tailored for different types of users: Administrators, Normal Students, and Privileged Users. Below is an overview of the system capabilities:

### 🛠️ Admin Features
- **Venue Management:** Create, update, remove, and search for venues. Admins can update venue capacities or status (e.g., mark as Blocked or under Maintenance).
- **Registration Approval:** Review, approve, or reject new user registrations.
- **Waitlist Management:** Monitor and manage users on the waitlist for various bookings.
- **Booking Management:** View all bookings across venues ("Slots View"), manually override booking statuses (Active, Waitlisted, Cancelled, Completed, Forfeited), and trigger automatic waitlist promotion.
- **User Management:** View registered user lists and edit user details.
- **Comprehensive Reports:** Generate system reports including Venue Utilization Summary, Booking Status Breakdown, Most Active Users, Peak Time Analysis, and historical action logs.

### 🎓 Student (Normal User) Features
- **Facility Booking:** Check available slots and book venues without overlap.
- **Waitlist Registration:** Join a waitlist for slots that are currently occupied.
- **Slots View:** Check the availability and booked times for all venues.
- **Manage Bookings:** View own booking history and cancel active bookings (which smoothly promotes waitlisted users).
- **Profile Management:** Update account credentials/password.

### 🌟 Privileged User Features
- **Express Booking:** Special privilege to override and displace existing active bookings for specific assigned venues. Displaced student bookings are automatically moved to the waitlist.
- **General Functions:** Ability to view slots, manage personal bookings, cancel bookings, and edit profile details similarly to normal users.

### 📝 New User & Guest Features
- **Account Registration:** Register for a new account by providing student details, requesting a Normal or Privileged role (with specific facility privileges).
- **Registration Status Tracking:** Check the status of an submitted registration (Pending, Approved, Rejected, or Suspended).
