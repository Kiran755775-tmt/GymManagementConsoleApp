Project name: Gym Membership Management System
Objective: To develop system to manage gym memberships, book gym classes, cancel bookings, view membership details,
           upgrade membership type.
Languages used: Java, Mysql 
Connector used: Mysql Connector for connectivity between application and mysql.

Features of project:
    1. Register new user for membership.
    2. Book a class.
    3. Cancel a Class Booking.
    4. View Membership Details.
    5. Upgrade membership type.
    6. Generate report(most booked class, no of active members, membership type distribution)

Register new member:
    If member_id not exists the new member will be added by checking the valid contact_info and membership_type.
Book a class:
    Class will be booked if valid class_name and seats_booked are valid .
Cancel a Booking:
    Class Booking will be cancelled if valid booking_id is entered.
View MemberShip Details:
    Membership Details and corresponding booking history will be printed using valid member_id.
Upgrade membership type:
    Membership type will be upgraded upon entering valid member_id and membership_type.
Generate report:
    Displays most booked class, active members and membership type distribution.

VALIDATION : 
    member_id -> exits or not
    booking_id -> exists or not
    class_name -> exits or not
    contact_info -> valid or not
    membership_type -> valid or not(gold,diamond, platinum)
    seats_booked-> checks whether available seats are enough to be booked or not

Tables used:
1.Classes  (class_id (int pk), class_name varchar, total_seats int, booked_seats int)
2.Members  (member_id(int pk), name varchar, contact_info varchar, membership_type varchar, membership_start_date date,membership_end_date date)
3.Bookings  (booking_id(int pk auto_inc),class_id(fk), member_id(fk),seats_booked int, booking_date datetime).


    