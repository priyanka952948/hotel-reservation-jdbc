import java.sql.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class HotelR {
    private static final String url = "jdbc:mysql://localhost:3306/hotel2_db";
    private static final String username = "root";
    private static final String password = "*******";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver load error: " + e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nHOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("6. Search Reservation by Date");
                System.out.println("7. Export Reservations to Text File");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> reserveRoom(connection, scanner);
                    case 2 -> viewReservations(connection);
                    case 3 -> getRoomNumber(connection, scanner);
                    case 4 -> updateReservation(connection, scanner);
                    case 5 -> deleteReservation(connection, scanner);
                    case 6 -> searchReservationByDate(connection, scanner);
                    case 7 -> exportReservationsToFile(connection);
                    case 0 -> {
                        exit();
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    private static void reserveRoom(Connection c, Scanner s) {
        try {
            s.nextLine();
            System.out.print("Enter Guest Name: ");
            String name = s.nextLine();
            System.out.print("Enter Contact Number: ");
            String contact = s.nextLine();
            System.out.print("Enter Room Number: ");
            int room = s.nextInt();

            String sql = "INSERT INTO reservations (guest_name, contact_number, room_number, reservation_date) VALUES (?, ?, ?, NOW())";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setInt(3, room);

            if (ps.executeUpdate() > 0)
                System.out.println("✅ Room reserved successfully!");
            else
                System.out.println("❌ Reservation failed.");
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    private static void viewReservations(Connection c) {
        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM reservations");
            while (rs.next()) {
                System.out.printf("ID: %d | Guest: %s | Contact: %s | Room: %d | Date: %s%n",
                        rs.getInt("reservation_id"),
                        rs.getString("guest_name"),
                        rs.getString("contact_number"),
                        rs.getInt("room_number"),
                        rs.getTimestamp("reservation_date"));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    private static void getRoomNumber(Connection c, Scanner s) {
        try {
            s.nextLine();
            System.out.print("Enter Guest Name: ");
            String guest = s.nextLine();

            String sql = "SELECT room_number FROM reservations WHERE guest_name = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, guest);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                System.out.println("Room Number: " + rs.getInt("room_number"));
            else
                System.out.println("No reservation found.");
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    private static void updateReservation(Connection c, Scanner s) {
        try {
            System.out.print("Enter Reservation ID to update: ");
            int id = s.nextInt();
            s.nextLine();
            System.out.print("Enter New Contact Number: ");
            String contact = s.nextLine();

            String sql = "UPDATE reservations SET contact_number = ? WHERE reservation_id = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, contact);
            ps.setInt(2, id);

            if (ps.executeUpdate() > 0)
                System.out.println("✅ Updated successfully!");
            else
                System.out.println("❌ No record found.");
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    private static void deleteReservation(Connection c, Scanner s) {
        try {
            System.out.print("Enter Reservation ID to delete: ");
            int id = s.nextInt();

            String sql = "DELETE FROM reservations WHERE reservation_id = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, id);

            if (ps.executeUpdate() > 0)
                System.out.println("✅ Deleted successfully!");
            else
                System.out.println("❌ No record found.");
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    private static void searchReservationByDate(Connection c, Scanner s) {
        try {
            s.nextLine();
            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = s.nextLine();

            String sql = "SELECT * FROM reservations WHERE DATE(reservation_date) = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("ID: %d | Guest: %s | Room: %d | Contact: %s | Date: %s%n",
                        rs.getInt("reservation_id"),
                        rs.getString("guest_name"),
                        rs.getInt("room_number"),
                        rs.getString("contact_number"),
                        rs.getTimestamp("reservation_date"));
            }
            if (!found)
                System.out.println("No reservations found on " + date);
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    private static void exportReservationsToFile(Connection c) {
        try (FileWriter fw = new FileWriter("reservations_export.txt")) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM reservations");
            while (rs.next()) {
                fw.write(String.format("ID: %d | Guest: %s | Contact: %s | Room: %d | Date: %s%n",
                        rs.getInt("reservation_id"),
                        rs.getString("guest_name"),
                        rs.getString("contact_number"),
                        rs.getInt("room_number"),
                        rs.getTimestamp("reservation_date")));
            }
            System.out.println("✅ Exported to reservations_export.txt");
        } catch (IOException | SQLException e) {
            System.out.println("Error exporting: " + e.getMessage());
        }
    }

    private static void exit() {
        System.out.print("Exiting");
        for (int i = 0; i < 3; i++) {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            System.out.print(".");
        }
        System.out.println("\nThank you!");
    }
}
