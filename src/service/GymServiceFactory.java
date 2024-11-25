package service;

import dao.GymDao;
import dao.GymDaoImpl;
import model.Member;

import java.sql.Date;
import java.util.Calendar;
import java.util.Scanner;

public class GymServiceFactory {

    private static Scanner scanner = new Scanner(System.in);

    // method to create GymService
    public static GymService createGymService() {
        GymDao gymDao = new GymDaoImpl();  // Initialize the GymDao
        return new GymService(gymDao);     // Return the GymService with the DAO injected
    }

    // New Registration
    public static void handleNewRegistration(GymService gymService) {
        System.out.println("----New Registration for Gym Membership----");
        System.out.print("Enter the Member Id: ");
        final int memberId = scanner.nextInt();
        scanner.nextLine(); // consume newline character
        System.out.print("Enter the name of the member: ");
        final String memberName = scanner.nextLine();
        System.out.print("Enter the mailId: ");
        String contactInfo = scanner.nextLine();

        // checking contact information whether valid or not
        while (!gymService.validateContactInfo(contactInfo)) {
            System.out.println("Given contactInfo is invalid");
            System.out.print("Enter the mailId: ");
            contactInfo = scanner.nextLine();
        }

        System.out.print("Enter the membership type(gold/diamond/platinum): ");
        String membershipType = scanner.nextLine();

        // checking membership type is valid or not
        while (!gymService.validateMembershipType(membershipType)) {
            System.out.println("Sorry, the membershipType " + membershipType + " is not valid. Please enter the right one");
            System.out.print("Enter the membership type(gold/diamond/platinum): ");
            membershipType = scanner.nextLine();
        }

        System.out.println("---Start Date of Membership Details---");
        System.out.print("Enter the string of start date (yyyy-mm-dd): ");
        String startDate = scanner.nextLine();
        final Date membershipStartDate = Date.valueOf(startDate);
        System.out.println("---End Date of Membership Details---");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(membershipStartDate);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        final Date membershipEndDate = new Date(calendar.getTimeInMillis());
        Member member = new Member(memberId, memberName, contactInfo, membershipType, membershipStartDate, membershipEndDate);
        gymService.newRegistration(member); // register the new member
    }

    // Book a Class
    public static void handleBookClass(GymService gymService) {
        System.out.println("------Booking a class------");
        System.out.print("Enter the class name (yoga/zumba/hiit/karate/gymnastics/pilates): ");
        //scanner.nextLine(); // consume newline character
        final String className = scanner.nextLine().toLowerCase();
        System.out.print("Number of seats you want to book: ");
        final int noOfSeats = scanner.nextInt();
        System.out.print("Enter the Member Id: ");
        final int memberId = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        gymService.bookClass(className, noOfSeats, memberId); // book a class
    }

    // Cancel a Class Booking
    public static void handleCancelBooking(GymService gymService) {
        System.out.println("-------Cancelling a Class Booking-------");
        System.out.print("Please enter the Booking Id to cancel: ");
        final int bookingId = scanner.nextInt();
        gymService.cancelBooking(bookingId); // cancel the booking
    }

    // View Membership Details
    public static void handleViewMembershipDetails(GymService gymService) {
        System.out.println("-------Viewing Membership Details--------");
        System.out.print("Enter the Member Id: ");
        final int memberId = scanner.nextInt();
        scanner.nextLine(); // consume newline character
        gymService.viewMembershipDetails(memberId); // retrieve member details
    }

    // Upgrade Membership Type
    public static void handleUpgradeMembership(GymService gymService) {
        System.out.println("----Upgrading Gym Membership----");
        System.out.print("Enter the Member Id: ");
        final int memberId = scanner.nextInt();
        scanner.nextLine(); // consume newline character

        System.out.print("Enter the type of the membership to convert to: ");
        String updatedMembershipType = scanner.nextLine();

        // checking the validity of membership type
        while (!gymService.validateMembershipType(updatedMembershipType)) {
            System.out.println("Given membershipType " + updatedMembershipType + " is not found.");
            System.out.print("Please Enter a valid membership type: ");
            updatedMembershipType = scanner.nextLine();
        }
        gymService.upgradeMembershipType(memberId, updatedMembershipType); // upgrade the membership type
    }

    // Generate Report
    public static void handleGenerateReport(GymService gymService) {
        gymService.generateReport(); // generate report
    }
}
