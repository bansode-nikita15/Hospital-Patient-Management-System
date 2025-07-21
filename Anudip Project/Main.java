//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String DB_URL = "jdbc:mysql://localhost:3306/Hospital_db";
    static final String DB_USER = "root";
    static final String DB_PASS = "root";
    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {

            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Connected to the database.");

            int choice;
            do {
                System.out.println("\n--- Hospital Patient Management System ---");
                System.out.println("1: Add Patient");
                System.out.println("2: View All Patients");
                System.out.println("3: Search Patient by Name");
                System.out.println("4: Update Patient");
                System.out.println("5: Delete Patient");
                System.out.println("6: Take Appointment");
                System.out.println("7: Cancel Appointment");
                System.out.println("0: Exit");
                System.out.print("Enter choice: ");
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1 -> addPatient();
                    case 2 -> viewPatients();
                    case 3 -> searchPatient();
                    case 4 -> updatePatient();
                    case 5 -> deletePatient();
                    case 6 -> takeAppointment();
                    case 7 -> cancelAppointment();
                    case 0 -> System.out.println("Exiting. Goodbye!");
                    default -> System.out.println("Invalid choice, try again.");
                }
            } while (choice != 0);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addPatient() throws SQLException {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(sc.nextLine());
        System.out.print("Enter Gender: ");
        String gender = sc.nextLine();
        System.out.print("Enter Diagnosis: ");
        String diagnosis = sc.nextLine();

        String sql = "INSERT INTO patients (name, age, gender, diagnosis) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, diagnosis);
            ps.executeUpdate();
            System.out.println("Patient added successfully.");
        }
    }

    static void viewPatients() throws SQLException {
        String sql = "SELECT * FROM patients";
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            System.out.println("\nID | Name | Age | Gender | Diagnosis");
            System.out.println("-------------------------------------");
            while (rs.next()) {
                System.out.printf("%d | %s | %d | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("diagnosis"));
            }
        }
    }

    static void searchPatient() throws SQLException {
        System.out.print("Enter name to search: ");
        String keyword = sc.nextLine();
        String sql = "SELECT * FROM patients WHERE name LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                System.out.println("\nSearch Results:");
                System.out.println("ID | Name | Age | Gender | Diagnosis");
                System.out.println("-------------------------------------");
                while (rs.next()) {
                    found = true;
                    System.out.printf("%d | %s | %d | %s | %s%n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getString("diagnosis"));
                }
                if (!found) {
                    System.out.println("No patients found with that name.");
                }
            }
        }
    }

    static void updatePatient() throws SQLException {
        System.out.print("Enter patient ID to update: ");
        int id = Integer.parseInt(sc.nextLine());

        // Check if patient exists
        String checkSql = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Patient with ID " + id + " does not exist.");
                    return;
                }
            }
        }

        System.out.print("Enter new name: ");
        String name = sc.nextLine();
        System.out.print("Enter new age: ");
        int age = Integer.parseInt(sc.nextLine());
        System.out.print("Enter new gender: ");
        String gender = sc.nextLine();
        System.out.print("Enter new diagnosis: ");
        String diagnosis = sc.nextLine();

        String sql = "UPDATE patients SET name = ?, age = ?, gender = ?, diagnosis = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, diagnosis);
            ps.setInt(5, id);
            ps.executeUpdate();
            System.out.println("Patient updated successfully.");
        }
    }

    static void deletePatient() throws SQLException {
        System.out.print("Enter patient ID to delete: ");
        int id = Integer.parseInt(sc.nextLine());
        String sql = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient deleted successfully.");
            } else {
                System.out.println("No patient found with that ID.");
            }
        }
    }

    static void takeAppointment() throws SQLException {
        System.out.print("Enter patient ID: ");
        int patientId = Integer.parseInt(sc.nextLine());
        System.out.print("Enter appointment date (YYYY-MM-DD HH:MM:SS): ");
        String apptDate = sc.nextLine();
        System.out.print("Enter reason for appointment: ");
        String reason = sc.nextLine();

        System.out.println("Available Doctors:");
        String docSql = "SELECT * FROM doctors";
        try (ResultSet rs = conn.createStatement().executeQuery(docSql)) {
            while (rs.next()) {
                System.out.printf("%d: %s (%s)%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialty"));
            }
        }

        System.out.print("Enter doctor ID to assign: ");
        int doctorId = Integer.parseInt(sc.nextLine());

        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, reason) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, apptDate);
            ps.setString(4, reason);
            ps.executeUpdate();
            System.out.println("Appointment scheduled successfully.");
        }
    }

    static void cancelAppointment() throws SQLException {
        System.out.println("Current Appointments:");
        String sql = """
            SELECT a.id, p.name AS patient, d.name AS doctor, a.appointment_date, a.reason
            FROM appointments a
            JOIN patients p ON a.patient_id = p.id
            JOIN doctors d ON a.doctor_id = d.id
            ORDER BY a.appointment_date
            """;
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%d: %s - %s with Dr. %s (Reason: %s)%n",
                        rs.getInt("id"),
                        rs.getString("appointment_date"),
                        rs.getString("patient"),
                        rs.getString("doctor"),
                        rs.getString("reason"));
            }
        }

        System.out.print("Enter appointment ID to cancel: ");
        int apptId = Integer.parseInt(sc.nextLine());

        String delSql = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(delSql)) {
            ps.setInt(1, apptId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Appointment canceled successfully.");
            } else {
                System.out.println("No appointment found with that ID.");
            }
        }
    }
}
