import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

// --- Abstraction for channels (ISP: one cohesive method, nothing forced) ---
interface NotificationChannel {
    void send(String user, String message);
}

class EmailChannel implements NotificationChannel {
    @Override
    public void send(String user, String message) {
        System.out.println("Sending EMAIL to " + user + ": " + message);
    }
}

class SmsChannel implements NotificationChannel {
    @Override
    public void send(String user, String message) {
        System.out.println("Sending SMS to " + user + ": " + message);
    }
}

class PushChannel implements NotificationChannel {
    @Override
    public void send(String user, String message) {
        System.out.println("Sending PUSH to " + user + ": " + message);
    }
}

// --- Abstraction for persistence (DIP: depend on interface, not JDBC directly) ---
interface NotificationRepository {
    void log(String user, String channel, String message);
}

class SQLiteNotificationRepository implements NotificationRepository {
    private final Connection connection;

    public SQLiteNotificationRepository(String dbPath) throws Exception {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    @Override
    public void log(String user, String channel, String message) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO logs (user, channel, message) VALUES (?, ?, ?)"
            );
            stmt.setString(1, user);
            stmt.setString(2, channel);
            stmt.setString(3, message);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to log notification", e);
        }
    }
}

// --- Orchestrator: SRP (only coordinates), OCP (open to new channels via registration) ---
class NotificationCenter {
    private final NotificationRepository repository;
    private final Map<String, NotificationChannel> channels = new HashMap<>();

    public NotificationCenter(NotificationRepository repository) {
        this.repository = repository;
    }

    public void registerChannel(String name, NotificationChannel channel) {
        channels.put(name, channel);
    }

    public void notify(String channelName, String user, String message) {
        NotificationChannel channel = channels.get(channelName);
        if (channel == null) {
            throw new IllegalArgumentException("Unknown channel: " + channelName);
        }
        channel.send(user, message);              // LSP: any channel is substitutable here
        repository.log(user, channelName, message);
    }
}

// --- Usage ---
public class Main {
    public static void main(String[] args) throws Exception {
        NotificationRepository repo = new SQLiteNotificationRepository("notifications.db");
        NotificationCenter center = new NotificationCenter(repo);

        center.registerChannel("email", new EmailChannel());
        center.registerChannel("sms", new SmsChannel());
        center.registerChannel("push", new PushChannel());

        center.notify("email", "user1", "Your order has shipped!");
    }
}