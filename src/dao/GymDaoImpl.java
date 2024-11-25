package dao;

import exception.InvalidQueryException;
import model.Member;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

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
    static Connection connection;

    static {
        connection = ConnectorFactory.getConnection();
    }

    //REGISTERING NEW USER
    @Override
    public void registerNewMember(Member member) {
        try (PreparedStatement registerMemberStatement = connection.prepareStatement(INSERT_QUERY)) {
            registerMemberStatement.setInt(1, member.getMemberId());
            registerMemberStatement.setString(2, member.getMemberName());
            registerMemberStatement.setString(3, member.getMailId());
            registerMemberStatement.setString(4, member.getMembershipType());
            registerMemberStatement.setDate(5, member.getMembershipStartDate());
            registerMemberStatement.setDate(6, member.getGetMembershipEndDate());
            int rowsAffected = registerMemberStatement.executeUpdate();
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
        try (PreparedStatement checkMemberByIdStatement = connection.prepareStatement(GET_MEMBER_BY_ID_QUERY)) {
            checkMemberByIdStatement.setInt(1, memberId);
            ResultSet memberResultSet = checkMemberByIdStatement.executeQuery();
            if (memberResultSet.next()) {
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
        try (PreparedStatement checkBookingByIdStatement = connection.prepareStatement(SELECT_CLASS_ID_QUERY)) {
            checkBookingByIdStatement.setInt(1, bookingId);
            ResultSet bookingResultSet = checkBookingByIdStatement.executeQuery();
            if (bookingResultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
        return false;
    }

    //method to fetch available seats for a given class id.
    @Override
    public int getAvailableSeats(int classId) {
        try (PreparedStatement fetchAvailableSeatsStatement = connection.prepareStatement(SEATS_AVAILABLE_QUERY)) {
            fetchAvailableSeatsStatement.setInt(1, classId);
            ResultSet seatsResultSet = fetchAvailableSeatsStatement.executeQuery();
            if (seatsResultSet.next()) {
                int totalSeats = seatsResultSet.getInt("total_seats");
                int bookedSeats = seatsResultSet.getInt("booked_seats");
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
        try (PreparedStatement updateSeatsStatement = connection.prepareStatement(CHANGING_SEATS_QUERY)) {
            updateSeatsStatement.setInt(1, newBookedSeats);
            updateSeatsStatement.setInt(2, classId);
            updateSeatsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new InvalidQueryException("Sql Query syntax is not valid");
        }
    }

    //EXTRACTS CLASS ID USING CLASS NAME FROM CLASSES TABLE
    @Override
    public int getClassIdByName(String className) {
        try (PreparedStatement fetchClassIdStatement = connection.prepareStatement(GET_CLASS_ID_QUERY)) {
            fetchClassIdStatement.setString(1, className);
            ResultSet classResultSet = fetchClassIdStatement.executeQuery();
            if (classResultSet.next()) {
                return classResultSet.getInt("class_id");
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
        try (PreparedStatement bookClassStatement = connection.prepareStatement(BOOK_QUERY);//this statement inserts data into books
             PreparedStatement checkSeatsStatement = connection.prepareStatement(SEATS_AVAILABLE_QUERY)) {
            int classId = getClassIdByName(className);
            checkSeatsStatement.setInt(1, classId);
            ResultSet seatsResultSet = checkSeatsStatement.executeQuery();
            if (seatsResultSet.next()) {
                int bookedSeats = seatsResultSet.getInt("booked_seats");
                // Updating the booked seats by adding the new bookings
                updateBookedSeats(classId, bookedSeats + noOfSeats); // Update with new booked seats count
            }
            //inserting data into bookings
            bookClassStatement.setInt(1, classId);
            bookClassStatement.setInt(2, noOfSeats);
            bookClassStatement.setInt(3, memberId);
            bookClassStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            bookClassStatement.executeUpdate();
            System.out.println("Your class has been booked successfully ");
        } catch (SQLException e) {
            logger.severe("Error booking class: " + e.getMessage());
            throw new InvalidQueryException("Error occurred while booking the class");
        }
    }


    //CANCELLING A CLASS USING BOOKING ID
    @Override
    public void cancelBooking(int bookingId) {
        try (PreparedStatement fetchBookingDetailsStatement = connection.prepareStatement(SELECT_CLASS_ID_QUERY); PreparedStatement cancelBookingStatement = connection.prepareStatement(CANCEL_BOOKING_QUERY); PreparedStatement fetchSeatsStatement = connection.prepareStatement(SEATS_AVAILABLE_QUERY); PreparedStatement updateSeatsStatement = connection.prepareStatement(CHANGING_SEATS_QUERY)) {
            fetchBookingDetailsStatement.setInt(1, bookingId);
            ResultSet bookingDetailsResultSet = fetchBookingDetailsStatement.executeQuery();

            if (bookingDetailsResultSet.next()) {
                //extracting class id using booking id
                int classId = bookingDetailsResultSet.getInt("class_id");
                //executing fetchSeatsStatement to extract booked_seats
                fetchSeatsStatement.setInt(1, classId);
                ResultSet seatsResultSet = fetchSeatsStatement.executeQuery();
                if (seatsResultSet.next()) {
                    //removing the seats booked in present booking from booked seats
                    int bookedSeats = seatsResultSet.getInt("booked_seats");
                    int seatsBookedNow = bookingDetailsResultSet.getInt("seats_booked");
                    updateSeatsStatement.setInt(1, bookedSeats - seatsBookedNow);
                    updateSeatsStatement.setInt(2, classId);
                    updateSeatsStatement.executeUpdate();
                    //deleting record from bookings table using booking id
                    cancelBookingStatement.setInt(1, bookingId);
                    cancelBookingStatement.executeUpdate();
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
        try (PreparedStatement fetchMembershipDetailsStatement = connection.prepareStatement(RETRIEVE_MEMBERSHIP_DETAILS_QUERY)) {

            fetchMembershipDetailsStatement.setInt(1, memberId);
            ResultSet resultSet = fetchMembershipDetailsStatement.executeQuery();
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(RETRIEVE_BOOKING_DETAILS_QUERY)) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {

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
        try (Statement statement = connection.createStatement(); //this statement executes active members
             Statement statement1 = connection.createStatement(); //this statement executes most booked class
             Statement statement2 = connection.createStatement(); //this statement executes membership type distribution
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
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