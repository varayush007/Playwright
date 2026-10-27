package tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.InventoryService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class InventoryServiceTest {

    private InventoryService service;

    @BeforeMethod
    public void setUp() {
        service = new InventoryService();
    }

    // 1. ADD STOCK: HAPPY PATHS

    @Test
    public void addStock_newProduct_storesQuantity() {
        service.addStock("P1", 10);

        assertEquals(service.getStock("P1"), 10);
    }

    @Test
    public void addStock_existingProduct_accumulatesQuantity() {
        service.addStock("P1", 10);
        service.addStock("P1", 5);

        assertEquals(service.getStock("P1"), 15);
    }

    @Test
    public void addStock_differentProducts_keepsStockSeparate() {
        service.addStock("P1", 10);
        service.addStock("P2", 20);

        assertEquals(service.getStock("P1"), 10);
        assertEquals(service.getStock("P2"), 20);
    }

    @Test
    public void addStock_oneUnit_storesOne() {
        service.addStock("P1", 1);

        assertEquals(service.getStock("P1"), 1);
    }

    @Test
    public void addStock_maxInteger_storesMaximumValue() {
        service.addStock("P1", Integer.MAX_VALUE);

        assertEquals(service.getStock("P1"), Integer.MAX_VALUE);
    }

    // 2. ADD STOCK: INVALID INPUTS

    @DataProvider(name = "invalidProductIds")
    public Object[][] invalidProductIds() {
        return new Object[][] {
                { null },
                { "" },
                { " " },
                { "   " },
                { "\t" },
                { "\n" }
        };
    }

    @Test(dataProvider = "invalidProductIds")
    public void addStock_invalidProductId_throwsException(String productId) {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock(productId, 10)
        );
    }

    @DataProvider(name = "invalidQuantities")
    public Object[][] invalidQuantities() {
        return new Object[][] {
                { 0 },
                { -1 },
                { -100 },
                { Integer.MIN_VALUE }
        };
    }

    @Test(dataProvider = "invalidQuantities")
    public void addStock_invalidQuantity_throwsException(int quantity) {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock("P1", quantity)
        );
    }

    @Test
    public void addStock_invalidInput_doesNotModifyExistingStock() {
        service.addStock("P1", 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock("P1", 0)
        );

        assertEquals(service.getStock("P1"), 10);
    }

    // 3. ADD STOCK: OVERFLOW

    @Test
    public void addStock_integerOverflow_doesNotCorruptStock() {
        service.addStock("P1", Integer.MAX_VALUE);

        assertThrows(
                ArithmeticException.class,
                () -> service.addStock("P1", 1)
        );

        assertEquals(service.getStock("P1"), Integer.MAX_VALUE);
    }

    // This test expects overflow protection that the original
    // implementation does not have. It should fail until fixed.

    // 4. RESERVE: HAPPY PATHS

    @Test
    public void reserve_sufficientStock_returnsTrueAndDeductsStock() {
        service.addStock("P1", 10);

        boolean result = service.reserve("P1", 4);

        assertTrue(result);
        assertEquals(service.getStock("P1"), 6);
    }

    @Test
    public void reserve_exactAvailableStock_returnsTrueAndLeavesZero() {
        service.addStock("P1", 10);

        boolean result = service.reserve("P1", 10);

        assertTrue(result);
        assertEquals(service.getStock("P1"), 0);
    }

    @Test
    public void reserve_oneUnitFromOneUnit_succeeds() {
        service.addStock("P1", 1);

        assertTrue(service.reserve("P1", 1));
        assertEquals(service.getStock("P1"), 0);
    }

    @Test
    public void reserve_multipleTimes_deductsCumulatively() {
        service.addStock("P1", 10);

        assertTrue(service.reserve("P1", 3));
        assertTrue(service.reserve("P1", 2));

        assertEquals(service.getStock("P1"), 5);
    }

    @Test
    public void reserve_allStockInMultipleRequests_succeeds() {
        service.addStock("P1", 10);

        assertTrue(service.reserve("P1", 4));
        assertTrue(service.reserve("P1", 6));

        assertEquals(service.getStock("P1"), 0);
    }

    // 5. RESERVE: INSUFFICIENT STOCK

    @Test
    public void reserve_moreThanAvailable_returnsFalseAndPreservesStock() {
        service.addStock("P1", 5);

        boolean result = service.reserve("P1", 6);

        assertFalse(result);
        assertEquals(service.getStock("P1"), 5);
    }

    @Test
    public void reserve_nonexistentProduct_returnsFalse() {
        assertFalse(service.reserve("UNKNOWN", 1));
    }

    @Test
    public void reserve_fromEmptyInventory_returnsFalse() {
        service.addStock("P1", 1);
        service.reserve("P1", 1);

        assertFalse(service.reserve("P1", 1));
        assertEquals(service.getStock("P1"), 0);
    }

    @Test
    public void reserve_failedAttempt_doesNotAffectLaterValidReservation() {
        service.addStock("P1", 5);

        assertFalse(service.reserve("P1", 6));
        assertTrue(service.reserve("P1", 5));

        assertEquals(service.getStock("P1"), 0);
    }

    // 6. RESERVE: INVALID INPUTS

    @Test(dataProvider = "invalidProductIds")
    public void reserve_invalidProductId_throwsException(String productId) {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.reserve(productId, 1)
        );
    }

    @Test(dataProvider = "invalidQuantities")
    public void reserve_invalidQuantity_throwsException(int quantity) {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.reserve("P1", quantity)
        );
    }

    @Test
    public void reserve_invalidInput_doesNotModifyStock() {
        service.addStock("P1", 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.reserve("P1", 0)
        );

        assertEquals(service.getStock("P1"), 10);
    }

    // 7. GET STOCK

    @Test
    public void getStock_existingProduct_returnsCurrentQuantity() {
        service.addStock("P1", 10);
        service.reserve("P1", 3);

        assertEquals(service.getStock("P1"), 7);
    }

    @Test
    public void getStock_nonexistentProduct_returnsZero() {
        assertEquals(service.getStock("UNKNOWN"), 0);
    }

    @Test
    public void getStock_nullProductId_returnsZero() {
        assertEquals(service.getStock(null), 0);
    }

    @Test
    public void getStock_blankProductId_returnsZero() {
        assertEquals(service.getStock(" "), 0);
    }

    @Test
    public void getStock_doesNotCreateNewProduct() {
        service.getStock("UNKNOWN");

        assertEquals(service.getStock("UNKNOWN"), 0);
    }

    // 8. INDEPENDENT PRODUCT STATE

    @Test
    public void reserve_oneProduct_doesNotChangeAnotherProduct() {
        service.addStock("P1", 10);
        service.addStock("P2", 20);

        service.reserve("P1", 5);

        assertEquals(service.getStock("P1"), 5);
        assertEquals(service.getStock("P2"), 20);
    }

    @Test
    public void invalidOperation_doesNotChangeOtherProducts() {
        service.addStock("P1", 10);
        service.addStock("P2", 20);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock("P1", -5)
        );

        assertEquals(service.getStock("P1"), 10);
        assertEquals(service.getStock("P2"), 20);
    }

    // 9. CONCURRENCY REGRESSION TEST
    // This is a stress test, not a deterministic proof of thread safety.
    // The original implementation is not thread safe.

    @Test
    public void concurrentReservations_neverReserveMoreThanAvailable()
            throws Exception {

        service.addStock("P1", 5);

        int threadCount = 2;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);

        try {
            Future<Boolean>[] results = new Future[threadCount];

            for (int i = 0; i < threadCount; i++) {
                results[i] = pool.submit(() -> {
                    ready.countDown();
                    start.await();
                    return service.reserve("P1", 4);
                });
            }

            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            int successes = 0;
            for (Future<Boolean> result : results) {
                if (result.get(5, TimeUnit.SECONDS)) {
                    successes++;
                }
            }

            assertEquals(successes, 1);
            assertEquals(service.getStock("P1"), 1);
        } finally {
            start.countDown();
            pool.shutdownNow();
        }
    }
}
