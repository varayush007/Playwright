package pages;

import java.util.HashMap;
import java.util.Map;

public class Validator {

    // User Reg

    public boolean isValid(String username, String email, int age) {

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        if (username.length() < 3 || username.length() > 15) {
            return false;
        }

        if (email == null || !email.contains("@")) {
            return false;
        }

        if (age < 18 || age > 60) {
            return false;
        }

        return true;
    }

    // Price Check

    public double calculateFinalPrice(double price, int discount) {
        if (price < 0 || discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Invalid input");
        }

        return price - (price * discount / 100.0);
    }

    // Password Check

    public boolean isValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasDigit = false;

        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }

        return hasDigit;
    }


    // Shopping Cart

    private final Map<String, Integer> items = new HashMap<>();

    public void addItem(String item, int quantity) {
        if (item == null || item.isEmpty() || quantity <= 0) {
            throw new IllegalArgumentException();
        }

        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public int getQuantity(String item) {
        return items.getOrDefault(item, 0);
    }

    public int getTotalItems() {
        int total = 0;

        for (int quantity : items.values()) {
            total += quantity;
        }

        return total;
    }


    // Retry Utility

    public boolean execute(java.util.function.Supplier<Boolean> task, int maxAttempts) {

        if (task == null || maxAttempts <= 0) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < maxAttempts; i++) {
            if (Boolean.TRUE.equals(task.get())) {
                return true;
            }
        }

        return false;
    }
}
