import service.GymService;
import service.GymServiceFactory;

import java.util.Scanner;

public class GymMembershipManagementSystemConsoleApp {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;
        // Create GymService using the factory
        GymService gymService = GymServiceFactory.createGymService();

        while (!exit) {
            gymService.menu(); // displays the list of options
            System.out.print("Please select any one option from above: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    GymServiceFactory.handleNewRegistration(gymService);
                    break;
                case 2:
                    GymServiceFactory.handleBookClass(gymService);
                    break;
                case 3:
                    GymServiceFactory.handleCancelBooking(gymService);
                    break;
                case 4:
                    GymServiceFactory.handleViewMembershipDetails(gymService);
                    break;
                case 5:
                    GymServiceFactory.handleUpgradeMembership(gymService);
                    break;
                case 6:
                    GymServiceFactory.handleGenerateReport(gymService);
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid entry: " + option + "\n Please enter the right option.");
                    break;
            }
        }
    }
}


//checking the given memberId exits or not
                   /* while (gymDao.getMemberById(memberId)) {
                        System.out.println("Member with " + memberId + " has already membership.");
                        System.out.print("Please Enter the new Member Id: ");
                        memberId = scanner.nextInt();
                    }*/


//checking the existence of class name
                   /* while (classId == 0) {
                        System.out.println("Sorry Your Class Name: " + className + " is not Found");
                        System.out.print("Please enter the right Class Name: ");
                        className = scanner.nextLine();
                        classId = gymDao.getClassIdByName(className);
                    }*/


/*while (!gymDao.hasSeatsAvailable(classId, noOfSeats)) {
                        System.out.println("Sorry. Given number of seats not available");
                        System.out.print("Please Enter Another Number : ");
                        noOfSeats = scanner.nextInt();
                    }*/

//checking the existence of member id
                    /*while (!gymDao.getMemberById(memberId1)) {
                        System.out.println("Sorry your Member Id: " + memberId1 + " is not found.");
                        System.out.print("Please enter the existing Member Id: ");
                        memberId1 = scanner.nextInt();
                    }*/

 /* //checking the existence of booking id
                    while (!gymDao.exitsBookingId(bookingId)) {
                        System.out.println("Sorry your Booking Id " + bookingId + " is not found.");
                        System.out.print("Enter the existing Booking Id: ");
                        bookingId = scanner.nextInt();
                    }*/

//checking the existence of member id
                    /*while (!gymDao.getMemberById(memberId2)) {
                            System.out.println("Sorry your member id: " + memberId2 + " is not found.");
                            System.out.print("Enter the Existing Member Id : ");
                            memberId2 = scanner.nextInt();
                            }*/
//checking the existence of member id
                   /* while (!gymDao.getMemberById(memberId3)) {
                            System.out.println("Sorry your Member Id: " + memberId3 + " is not found.");
                            System.out.print("Please enter the existing Member Id: ");
                            memberId3 = scanner.nextInt();
                            }*/