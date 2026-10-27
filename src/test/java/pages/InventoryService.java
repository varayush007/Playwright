package pages;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {

    private final Map<String, Integer> inventory = new HashMap<>();

    public void addStock(String productId, int quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        inventory.put(
                productId,
                inventory.getOrDefault(productId, 0) + quantity
        );
    }

    public boolean reserve(String productId, int quantity) {
        if (productId == null || productId.isBlank() || quantity <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        int available = inventory.getOrDefault(productId, 0);

        if (available < quantity) {
            return false;
        }

        inventory.put(productId, available - quantity);
        return true;
    }

    public int getStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }
}