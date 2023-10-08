package jdbc_assignment_6;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmployeeDatabaseApp {

    // Database URL, username, and password
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employee";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Step 1: Connect to the database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Step 2: Insert three employee records into the "employee" table
            insertEmployeeRecords(connection);

            // Step 3: Retrieve and print all employee records
            System.out.println("Employee records before salary updates:");
            retrieveAndPrintEmployeeRecords(connection);

            // Step 4: Begin a transaction
            connection.setAutoCommit(false);

            // Step 5: Update the salary of an employee with a specific id (increase salary by 10%)
            updateEmployeeSalary(connection, 1, 1.10);

            // Step 6: Update the salary of another employee with a specific id (decrease salary by 5%)
            updateEmployeeSalary(connection, 2, 0.95);

            // Step 7: Commit the transaction to save the changes to the database
            connection.commit();

            // Step 8: Retrieve and print all employee records again
            System.out.println("Employee records after salary updates:");
            retrieveAndPrintEmployeeRecords(connection);
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
            try {
                if (connection != null) {
                    // Rollback the transaction in case of an exception
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    // Close the database connection
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void insertEmployeeRecords(Connection connection) throws SQLException {
        String insertSQL = "INSERT INTO emp (id, name, salary) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            // Insert three employee records
            for (int i = 1; i <= 3; i++) {
                preparedStatement.setInt(1, i);
                preparedStatement.setString(2, "Employee " + i);
                preparedStatement.setDouble(3, 50000.0); // Initial salary is 50000.0
                preparedStatement.executeUpdate();
            }
        }
    }

    private static void retrieveAndPrintEmployeeRecords(Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM emp";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                System.out.println("ID: " + id + ", Name: " + name + ", Salary: " + salary);
            }
        }
    }

    private static void updateEmployeeSalary(Connection connection, int id, double salaryMultiplier) throws SQLException {
        String updateSQL = "UPDATE emp SET salary = salary * ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setDouble(1, salaryMultiplier);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
}
