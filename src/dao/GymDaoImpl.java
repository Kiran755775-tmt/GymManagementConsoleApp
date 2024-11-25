package dao;

import java.util.logging.Logger;

import exception.InvalidQueryException;
import model.Member;

import java.io.*;
import java.sql.*;

public class GymDaoImpl implements GymDao {
    private static final Logger logger = Logger.getLogger(GymDaoImpl.class.getName());
    //NEW REGISTRATION AND UPGRADATION QUERIES
    private static final String INSERT_QUERY = "insert into members (member_id,name,contact_info,membership_type,membership_start_date,membership_end_date) values(?,?,?,?,?,?)";
    private static final String UPDATE_QUERY = "update members set membership_type= ? where member_id = ?";
    private static final String GET_MEMBER_BY_ID_QUERY = "select* from members where member_id=?";
    private static final String GET_CLASS_ID_QUERY = "select class_id from classes where class_name=?";

    //BOOK AND CANCEL BOOKING QUERIES
    private static final String BOOK_QUERY = "insert into bookings(class_id,seats_booked,member_id,booking_date) values(?,?,?,? )";
    private static final String SEATS_AVAILABLE_QUERY = "select total_seats,booked_seats from classes where class_id=?";
    private static final String CHANGING_SEATS_QUERY = "update classes set booked_seats=? where class_id=?";
    private static final String SELECT_CLASS_ID_QUERY = "select* from bookings where booking_id=?";
    private static final String CANCEL_BOOKING_QUERY = "delete from bookings where booking_id=?";

    //RETRIEVING MEMBER DETAILS QUERIES
    private static final String RETRIEVE_MEMBERSHIP_DETAILS_QUERY = "select member_id,name,contact_info,membership_type,membership_start_date,membership_end_date from members where member_id=?";
    private static final String RETRIEVE_BOOKING_DETAILS_QUERY = "select b.booking_id,c.class_name, b.seats_booked, b.booking_date from bookings b join classes c on b.class_id= c.class_id where member_id=?";

    // GENERATING REPORT QUERIES
    private static final String RETRIEVE_ACTIVE_MEMBERS_QUERY = "select count(*) from members where membership_end_date>curdate()";
    private static final String RETRIEVE_MOST_BOOKED_CLASS_QUERY = "select c.class_name, b.seats_booked from bookings b join classes c on b.class_id= c.class_id  order by b.seats_booked desc limit 1";
    private static final String RETRIEVE_MEMBERSHIP_TYPE_DISTRIBUTION_QUERY = "select membership_type, count(*) from members group by membership_type";


    //REGISTERING NEW USER
    @Override
    public void registerNewMember(Member member) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)
        ) {
            if (getMemberById(member.getMemberId()) != false) {
                logger.info("Member with " + member.getMemberId() + " has already membership.\n Please enter another one");
                return;
            }
            if (member.getMemberName().length() > 20) {
                logger.info("Member Name length should not exceed 20 characters");
            }
            preparedStatement.setInt(1, member.getMemberId());
            preparedStatement.setString(2, member.getMemberName());
            preparedStatement.setString(3, member.getMailId());
            preparedStatement.setString(4, member.getMembershipType());
            preparedStatement.setDate(5, member.getMembershipStartDate());
            preparedStatement.setDate(6, member.getGetMembershipEndDate());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Your new membership has been registered Succesfully");
            }
        } catch (SQLException e) {
            logger.severe("Error registering new member " + e.getMessage());
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
    }

    //CHECKS WHETHER GIVEN MEMBER ID IS PRESENT OR NOT IN MEMBERS TABLE
    @Override
    public boolean getMemberById(int memberId) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MEMBER_BY_ID_QUERY)
        ) {
            preparedStatement.setInt(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
        return false;
    }

    //CHECKS WHETHER GIVEN BOOKING ID IS PRESENT OR NOT IN  BOOKINGS TABLE
    @Override
    public boolean exitsBookingId(int bookingId) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CLASS_ID_QUERY)
        ) {
            preparedStatement.setInt(1, bookingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
        return false;
    }

    //method to fetch available seats for a given class id.
    private int getAvailableSeats(int classId) {
        try (
                Connection connection = ConnectorFactory.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SEATS_AVAILABLE_QUERY)
        ) {
            preparedStatement.setInt(1, classId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int totalSeats = resultSet.getInt("total_seats");
                int bookedSeats = resultSet.getInt("booked_seats");
                return totalSeats - bookedSeats;//return available seats
            }

        } catch (SQLException e) {
            logger.severe("Error while checking available seats " + e.getMessage());
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
        return 0;
    }

    //method to update the seats for a given class id

    private void updateBookedSeats(int classId, int newBookedSeats) {
        try (
                Connection connection = ConnectorFactory.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(CHANGING_SEATS_QUERY)
        ) {
            preparedStatement.setInt(1, newBookedSeats);
            preparedStatement.setInt(2, classId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
    }

    //EXTRACTS CLASS ID USING CLASS NAME FROM CLASSES TABLE
    @Override
    public int getClassIdByName(String className) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CLASS_ID_QUERY)) {
            preparedStatement.setString(1, className);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("class_id");
            }
        } catch (SQLException e) {
            logger.severe("Error while extracting class id " + e.getMessage());
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
        return 0;
    }

    //BOOKING A  CLASS USING CLASS ID, NO OF SEATS, MEMBER ID
    @Override
    public void bookClass(String className, int noOfSeats, int memberId) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(BOOK_QUERY);//this statement inserts data into books
             PreparedStatement selectSeatsStmt = connection.prepareStatement(SEATS_AVAILABLE_QUERY)
        ) {
            int classId = getClassIdByName(className);
            if (classId == 0) {
                logger.info("Sorry your class name is invalid. Enter the correct class name.");
                return;
            }
            int availableSeats = getAvailableSeats(classId);
            if (availableSeats < noOfSeats) {
                logger.info("Not Enough seats available. We have only " + availableSeats + " available seats");
                return;
            }
            if (getMemberById(memberId) == false) {
                logger.info("Invalid member id. Please enter the correct one");
                return;
            }
            selectSeatsStmt.setInt(1, classId);
            ResultSet resultSet = selectSeatsStmt.executeQuery();
            if (resultSet.next()) {
                int bookedSeats = resultSet.getInt("booked_seats");
                // Updating the booked seats by adding the new bookings
                updateBookedSeats(classId, bookedSeats + noOfSeats); // Update with new booked seats count
            }
            //inserting data into bookings
            preparedStatement.setInt(1, classId);
            preparedStatement.setInt(2, noOfSeats);
            preparedStatement.setInt(3, memberId);
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
            System.out.println("Your class has been booked successfully ");
        } catch (SQLException e) {
            logger.severe("Error booking class: " + e.getMessage());
            throw new InvalidQueryException("Error occurred while booking the class");
        }
    }


    //CANCELLING A CLASS USING BOOKING ID
    @Override
    public void cancelBooking(int bookingId) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CLASS_ID_QUERY);
             PreparedStatement preparedStatement1 = connection.prepareStatement(CANCEL_BOOKING_QUERY);
             PreparedStatement preparedStatement2 = connection.prepareStatement(SEATS_AVAILABLE_QUERY);
             PreparedStatement preparedStatement3 = connection.prepareStatement(CHANGING_SEATS_QUERY)
        ) {
            preparedStatement.setInt(1, bookingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!exitsBookingId(bookingId)) {
                logger.info("Sorry given booking id doesn't exists. Please enter the existing one");
            }
            if (resultSet.next()) {
                //extracting class id using booking id
                int classId = resultSet.getInt("class_id");
                //executing preparedStatement2 to extract booked_seats
                preparedStatement2.setInt(1, classId);
                ResultSet resultSet1 = preparedStatement2.executeQuery();
                if (resultSet1.next()) {
                    //removing the seats booked in present booking from booked seats
                    int bookedSeats = resultSet1.getInt("booked_seats");
                    int seatsBookedNow = resultSet.getInt("seats_booked");
                    preparedStatement3.setInt(1, bookedSeats - seatsBookedNow);
                    preparedStatement3.setInt(2, classId);
                    preparedStatement3.executeUpdate();
                    //deleting record from bookings table using booking id
                    preparedStatement1.setInt(1, bookingId);
                    preparedStatement1.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.severe("Error during the cancellation process: " + e.getMessage());
            throw new InvalidQueryException("SQL Query syntax is not valid");
        }

    }

    //RETRIEVING MEMBER DETAILS USING MEMBER ID
    @Override
    public void viewMembershipDetails(int memberId) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(RETRIEVE_MEMBERSHIP_DETAILS_QUERY)
        ) {
            if (getMemberById(memberId) == false) {
                logger.info("Sorry. There is no membership with this id.Please enter existing one");
                return;
            }
            preparedStatement.setInt(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Member Id: " + memberId);
                System.out.println("Member Name: " + resultSet.getString("name"));
                System.out.println("Contact Info: " + resultSet.getString("contact_info"));
                System.out.println("Membership Type: " + resultSet.getString("membership_type"));
                System.out.println("Membership Start Date: " + resultSet.getDate("membership_start_date"));
                System.out.println("Membership End Date: " + resultSet.getDate("membership_end_date"));
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving membership details: " + e.getMessage());
            throw new InvalidQueryException("SQL Query syntax is not valid");
        }
    }

    //FETCHING BOOK HISTORY
    @Override
    public void bookingDetails(int memberId) {
        try (
                Connection connection = ConnectorFactory.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(RETRIEVE_BOOKING_DETAILS_QUERY)
        ) {
            preparedStatement.setInt(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean hasBooking = false;
            while (resultSet.next()) {
                hasBooking = true;
                System.out.println("---Booking History---");
                System.out.println("Booking Id: " + resultSet.getInt("b.booking_id"));
                System.out.println("Class Name: " + resultSet.getString("c.class_name"));
                System.out.println("Seats Booked: " + resultSet.getInt("b.seats_booked"));
                System.out.println("Booking Date: " + resultSet.getTimestamp("b.booking_date"));
            }
            if (!hasBooking) {
                System.out.println(" Given member id " + memberId + " has no booking details");
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving booking details: " + e.getMessage());
            throw new InvalidQueryException("SQL Query syntax is not valid");
        }
    }

    //UPGRADING THE MEMBERSHIP TYPE USING MEMBER ID AND MEMBERSHIP TYPE
    @Override
    public void upgradeMembership(int memberId, String membershipType) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)
        ) {
            if (getMemberById(memberId) == false) {
                System.err.println("Sorry. There is no membership with this id.Please enter existing one");
                return;
            }
            preparedStatement.setInt(2, memberId);
            preparedStatement.setString(1, membershipType);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Error upgrading membership for member ID " + memberId + ": " + e.getMessage());
            throw new InvalidQueryException("SQL query syntax is not valid or update failed");
        }
    }

    //GENERATING THE REPORT USING BOOKINGS, MEMBERS AND CLASSES Data
    @Override
    public void generateReport() {
        File file = new File("GymManagement");
        try (
                Connection connection = ConnectorFactory.getConnection();
                Statement statement = connection.createStatement(); //this statement executes active members
                Statement statement1 = connection.createStatement(); //this statement executes most booked class
                Statement statement2 = connection.createStatement(); //this statement executes membership type distribution
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))
        ) {
            //fetching number of active numbers
            ResultSet resultSet = statement.executeQuery(RETRIEVE_ACTIVE_MEMBERS_QUERY);
            if (resultSet.next()) {
                //saving into file
                bufferedWriter.write("Active Members: " + resultSet.getInt("count(*)"));
                bufferedWriter.newLine();
                System.out.println("Active Members: " + resultSet.getInt("count(*)"));
            } else {
                System.out.println("No active members");
            }
            //fetching the most booked class
            ResultSet resultSet1 = statement1.executeQuery(RETRIEVE_MOST_BOOKED_CLASS_QUERY);
            if (resultSet1.next()) {
                bufferedWriter.write("Most booked class: " + resultSet1.getString("c.class_name"));
                bufferedWriter.newLine();
                System.out.println("Most booked class: " + resultSet1.getString("c.class_name"));
            }

            //fetching the membership type distribution
            ResultSet resultSet2 = statement2.executeQuery(RETRIEVE_MEMBERSHIP_TYPE_DISTRIBUTION_QUERY);
            System.out.println("-----MEMBERSHIP TYPE DISTRIBUTION-----");
            while (resultSet2.next()) {
                bufferedWriter.write(resultSet2.getString("membership_type") + " - " + resultSet2.getInt("count(*)"));
                bufferedWriter.newLine();
                System.out.println(resultSet2.getString("membership_type") + " - " + resultSet2.getInt("count(*)"));
            }
        } catch (SQLException | IOException e) {
            logger.severe("Error generating the report: " + e.getMessage());
            throw new InvalidQueryException("Error generating report. Please check the logs.");
        }
    }
}
































    /*//CHECKING WHETHER REQUIRED NUMBER OF SEATS AVAILABLE OR NOT
    @Override
    public boolean hasSeatsAvailable(int classId, int noOfSeats) {
        try (Connection connection = ConnectorFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SEATS_AVAILABLE_QUERY)
        ) {
            preparedStatement.setInt(1, classId);
            ResultSet ruleset = preparedStatement.executeQuery();
            if (ruleset.next()) {
                int totalSeats = ruleset.getInt("total_seats");
                int bookedSeats = ruleset.getInt("booked_seats");
                int availableSeats = totalSeats - bookedSeats;
                return availableSeats >= noOfSeats;
            }
        } catch (SQLException e) {
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
        return false;
    }*/