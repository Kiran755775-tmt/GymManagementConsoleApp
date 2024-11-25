package service;

import dao.GymDao;
import dao.GymDaoImpl;
import model.Member;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class GymService {

    private final GymDao gymDao;

    // Constructor that accepts the GymDao instance as a parameter
    public GymService(GymDao gymDao) {
        this.gymDao = gymDao;
    }
    private static final Logger logger = Logger.getLogger(GymDaoImpl.class.getName());
    // MENU LIST OF OPTIONS
    public static void menu() {
        System.out.println("------GYM MEMBERSHIP MANAGEMENT SYSTEM------");
        System.out.println("1: Register for Membership");
        System.out.println("2: Book a class");
        System.out.println("3: Cancel a Class Booking");
        System.out.println("4: View Membership Details");
        System.out.println("5: Upgrade Membership");
        System.out.println("6: Generate Monthly Report");
        System.out.println("7: Exit the System");
    }

    // REGISTRATION FOR THE NEW MEMBERSHIP
    public void newRegistration(Member member) {
        if (gymDao.getMemberById(member.getMemberId()) != false) {
            logger.info("Member with " + member.getMemberId() + " has already membership.\n Please enter another one");
            return;
        }
        if (member.getMemberName().length() > 20) {
            logger.info("Member Name length should not exceed 20 characters");
        }
        gymDao.registerNewMember(member); // registers the new member
    }

    // BOOKING A CLASS
    public void bookClass(String className, int noOfSeats, int memberId) {
        int classId = gymDao.getClassIdByName(className);
        if (classId == 0) {
            logger.info("Sorry your class name is invalid. Enter the correct class name.");
            return;
        }
        int availableSeats = gymDao.getAvailableSeats(classId);
        if (availableSeats < noOfSeats) {
            logger.info("Not Enough seats available. We have only " + availableSeats + " available seats");
            return;
        }
        if (gymDao.getMemberById(memberId) == false) {
            logger.info("Invalid member id. Please enter the correct one");
            return;
        }
        gymDao.bookClass(className, noOfSeats, memberId); // booking the class
    }

    // CANCELLING A CLASS BOOKING
    public void cancelBooking(int bookingId) {
        if (!gymDao.exitsBookingId(bookingId)) {
            logger.info("Sorry given booking id doesn't exists. Please enter the existing one");
        }
        gymDao.cancelBooking(bookingId); // cancelling the booking
        System.out.println("Your booking has been cancelled successfully");
    }

    // FETCH THE DETAILS OF MEMBER
    public void viewMembershipDetails(int memberId) {
        if (gymDao.getMemberById(memberId) == false) {
            logger.info("Sorry. There is no membership with this id.Please enter existing one");
            return;
        }
        gymDao.viewMembershipDetails(memberId); // fetches the member details
        gymDao.bookingDetails(memberId); // fetches the booking history of the member
    }

    // UPDATING THE EXISTED MEMBERSHIP TYPE
    public void upgradeMembershipType(int memberId, String updatedMembershipType) {
        if (gymDao.getMemberById(memberId) == false) {
            System.err.println("Sorry. There is no membership with this id.Please enter existing one");
            return;
        }
        gymDao.upgradeMembership(memberId, updatedMembershipType); // upgrading the membership type using member id
        System.out.println("Your membership has been upgraded successfully");
    }

    // VALIDATING THE MEMBERSHIP TYPE
    public static boolean validateMembershipType(String membershipType) {
        List<String> membershipTypes = Arrays.asList("gold", "platinum", "diamond");
        return membershipTypes.stream().anyMatch(type -> type.equalsIgnoreCase(membershipType));
    }

    // VALIDATING THE CONTACT INFORMATION
    public static boolean validateContactInfo(String contactInfo) {
        String mailRegex = "[a-zA-Z0-9][a-zA-Z0-9_.]*@[a-zA-Z]+[.[a-zA-Z]+]+";
        Pattern pattern = Pattern.compile(mailRegex);
        Matcher matcher = pattern.matcher(contactInfo);
        return matcher.matches();
    }

    // GENERATE REPORT
    public void generateReport() {
        gymDao.generateReport(); // generates the report
    }
}
