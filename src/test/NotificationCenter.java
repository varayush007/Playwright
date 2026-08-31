import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class NotificationCenter {
    private Connection dbConnection;

    public NotificationCenter() throws Exception {
        // Directly creates a concrete DB connection inside the class
        this.dbConnection = DriverManager.getConnection("jdbc:sqlite:notifications.db");
    }

    public void notify(String channel, String user, String message) throws Exception {
        if (channel.equals("email")) {
            System.out.println("Sending EMAIL to " + user + ": " + message);
            // smtp logic here...
        } else if (channel.equals("sms")) {
            System.out.println("Sending SMS to " + user + ": " + message);
            // sms gateway logic here...
        } else if (channel.equals("push")) {
            System.out.println("Sending PUSH to " + user + ": " + message);
            // push notification logic here...
        } else {
            throw new IllegalArgumentException("Unknown channel");
        }

        // Logging tightly coupled into the same method
        PreparedStatement stmt = dbConnection.prepareStatement(
                "INSERT INTO logs (user, channel, message) VALUES (?, ?, ?)"
        );
        stmt.setString(1, user);
        stmt.setString(2, channel);
        stmt.setString(3, message);
        stmt.executeUpdate();
    }
}