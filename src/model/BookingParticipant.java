/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
public class BookingParticipant {
    private String studentId;
    private AttendanceStatus attendanceStatus;

    public BookingParticipant() {
        this.attendanceStatus = AttendanceStatus.NOT_MARKED;
    }

    public BookingParticipant(String studentId) {
        this.studentId = studentId;
        this.attendanceStatus = AttendanceStatus.NOT_MARKED;
    }

    public BookingParticipant(String studentId, AttendanceStatus attendanceStatus) {
        this.studentId = studentId;
        this.attendanceStatus = attendanceStatus;
    }

    public String getStudentId() {
        return studentId;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    @Override
    public String toString() {
        return "BookingParticipant{" +
                "studentId='" + studentId + '\'' +
                ", attendanceStatus=" + attendanceStatus +
                '}';
    }
}