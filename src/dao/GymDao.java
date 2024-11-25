package dao;

import model.Member;


public interface GymDao {
    //METHODS TO MANAGE GYM MEMBERSHIP MANAGAMENT SYSTEM

    void registerNewMember(Member member);

    void bookClass(String className, int noOfSeats, int memberId);

    void cancelBooking(int bookingId);

    void viewMembershipDetails(int memberId);

    void bookingDetails(int memberId);

    void upgradeMembership(int memberId, String membershipType);

    void generateReport();

    boolean getMemberById(int memberId);

    boolean exitsBookingId(int bookingId);

    int getClassIdByName(String className);
}





































