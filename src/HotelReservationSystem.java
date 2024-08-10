
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;
public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    //since these three variables are sensitive and for security reasons these are made private
    // ,and they are made static to access them anywhere in the class without creation of object
    //in this particular class

    private static final String username = "root";

    private static final String password = "vishnu@2627";

    public static void main(String[] args)  {
        try
        {
            //to load the drivers to connect to the database which are present in com.mysql package
            Class.forName("com.mysql.cj.jdbc.Driver");

        }
        catch (ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        //establishing the connection
        try
        {
            Connection connection = DriverManager.getConnection(url,username,password);

            System.out.println("HOTEL RESERVATION SYSTEM");
            Scanner sc = new Scanner(System.in);
            System.out.println("1.Reserve a room");
            System.out.println("2.View Reservation");
            System.out.println("3.Get Room Number");
            System.out.println("4.Update Reservations");
            System.out.println("5.Delete Reservations");
            System.out.println("0.EXIT");


          boolean choice = true;

            while(choice) {
                System.out.println("enter the option:");
                int option = sc.nextInt();
                switch (option)
                {
                    case 1:reserveRoom(connection,sc);
                        break;
                    case 2:viewReservations(connection);
                        break;
                    case 3:getRoomNumber(connection,sc);
                    break;
                    case 4:updateReservation(connection,sc);
                    break;
                    case 5:deleteReservation(connection,sc);
                    break;
                    case 6:exit();
                    break;
                    default:
                        System.out.println("enter valid number");
                        break;
                }
                System.out.println("do you want to continue (true/false)");
               choice = sc.nextBoolean();
            }
        }catch (SQLException e)
        {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection,Scanner scanner)
    {
        System.out.println("Enter guest name");
        String guestName = scanner.next();
        scanner.nextLine();
        System.out.println("enter room number:");
        int roomNumber = scanner.nextInt();
        System.out.println("enter contact number");
        String contactNumber = scanner.next();

        String sql = "INSERT INTO reservations (guest_name, room_number, contact_number, reservation_date) " +
                "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "', NOW())";
        try
        {
            //to execute the sql queries we use Statement interface
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(sql);
            if(affectedRows>0)
            {
                System.out.println("Reservation successful");
            }
            else
            {
                System.out.println("reservation failed");
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    private static void viewReservations(Connection connection) throws SQLException
    {
        String sql = "SELECT * FROM reservations;";
        try
        {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            System.out.println("current reservations");
            System.out.println("*.................*...............*...............*...................*........................*");
            System.out.println("|Reservation id   | guest         | Room Number   |  Contact Number   | Reservation date       |");
            System.out.println("*.................*...............*...............*...................*........................*");

            while(rs.next())
            {

                int reservation_id = rs.getInt("reservation_id");
                String guest_name  = rs.getString("guest_name");
                int room_number = rs.getInt("room_number");
                String contact_number = rs.getString("contact_number");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                //format amd display the reservations
                System.out.printf("|%-14d |%-15s| %-13d | %-20s | %-19s |",
                        reservation_id,guest_name,room_number,contact_number,reservationDate);
                System.out.println();
            }

            System.out.println("*.................*...............*...............*...................*........................*");


        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        System.out.println("Enter reservation ID:");
        int reservationId = scanner.nextInt();
        System.out.println("Enter guest name:");
        String guestName = scanner.next();
        String sql = "SELECT room_number FROM reservations WHERE reservation_id = " + reservationId +
                " AND guest_name = '" + guestName + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                System.out.println("Room number for reservation id " + reservationId + " and guest " + guestName + " is: " + roomNumber);
            } else {
                System.out.println("Reservation not found for the given ID and guest name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void updateReservation(Connection connection,Scanner scanner)
    {
        try
        {
            System.out.println("enter reservation id to update:");
            int reservationId = scanner.nextInt();
            scanner.nextLine();
            if(reservationExists(connection, reservationId))
            {
                System.out.println("reservation not found for the given id");
                return;
            }
            System.out.println("enter new guest name");
            String newGuestName = scanner.nextLine();
            System.out.println("enter new room number");
            int roomNumber = scanner.nextInt();
            System.out.print("enter new contact number");
            String newContactNumber = scanner.next();
            String sql = "UPDATE reservations SET guest_name = '"+newGuestName+"',"+"room_number = '"+roomNumber+"',"+"contact_number = '"+newContactNumber+"'"+"WHERE reservation_id = '"+reservationId+"'";
            try
            {
                Statement statement = connection.createStatement();
                int affectedRows  = statement.executeUpdate(sql);
                if (affectedRows>0)
                {
                    System.out.println("reservation updated successfully");
                }
                else
                {
                    System.out.println("reservation update failed");
                }

            }
            catch (SQLException e)
            {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    private static void deleteReservation(Connection connection,Scanner scanner)
    {
        try
        {
            System.out.println("enter reservation to delete");
            int reservationId = scanner.nextInt();
            if (reservationExists(connection, reservationId))
            {
                System.out.println("reservation not found for the given id");
                return;
            }
            String sql = "DELETE FROM reservations WHERE reservation_id = "+reservationId;
            try
            {
                Statement statement = connection.createStatement();
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows>0)
                {
                    System.out.println("reservation deleted successfully");

                }
                else
                {
                    System.out.println("reservation deletion failed");
                }
            }
            catch (SQLException e)
            {
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    private static boolean reservationExists(Connection connection,int reservationId)
    {

            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId;
            try
            {
                Statement statement = connection.createStatement();
                ResultSet rs  = statement.executeQuery(sql);
                return !rs.next();
            }
            catch (SQLException e)
            {
                System.out.println(e.getMessage());
                return true;
            }
    }

    private static void exit() throws InterruptedException
    {
        System.out.println("exiting system");
        int i = 5;
        while (i!=0)
        {
            System.out.println(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("thank you for using hotel management system");
    }
}
