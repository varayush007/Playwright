package pages;

public class OrderService {

    private final InventoryRepository inventoryRepository;

    public OrderService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public boolean placeOrder(String productId, int quantity) {
        if (productId == null || productId.isBlank()
                || quantity <= 0) {
            throw new IllegalArgumentException("Invalid order");
        }

        int stock = inventoryRepository.getStock(productId);

        if (stock < quantity) {
            return false;
        }

        return true;
    }
}
