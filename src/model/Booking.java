package model;

import java.sql.Timestamp;

public class Booking {
    //ATTRIBUTES
    private int bookingId;
    private int classId;
    private int memberId;
    private int numberOfSeats;
    private Timestamp bookingDate;

    //GETTERS AND SETTERS
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    //CONSTRUCTORS
    public Booking(int bookingId, int classId, int memberId, int numberOfSeats, Timestamp bookingDate) {
        this.bookingId = bookingId;
        this.classId = classId;
        this.memberId = memberId;
        this.numberOfSeats = numberOfSeats;
        this.bookingDate = bookingDate;
    }

    public Booking(int classId, int memberId, int numberOfSeats, Timestamp bookingDate) {
        this.classId = classId;
        this.memberId = memberId;
        this.numberOfSeats = numberOfSeats;
        this.bookingDate = bookingDate;
    }

    public Booking() {
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", classId=" + classId +
                ", memberId=" + memberId +
                ", numberOfSeats=" + numberOfSeats +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
