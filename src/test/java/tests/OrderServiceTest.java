package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.InventoryRepository;
import pages.OrderService;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    private InventoryRepository repository;
    private OrderService orderService;

    @BeforeMethod
    public void beforeMethod(){
        repository = mock(InventoryRepository.class);
        orderService = new OrderService(repository);
    }

    @Test
    public void validOrderWithSufficientStock(){
        when(repository.getStock("Apple")).thenReturn(10);
        boolean result = orderService.placeOrder("Apple",7);
        Assert.assertTrue(result);
        verify(repository).getStock("Apple");
    }

    @Test
    public void validOrderWithInSufficientStock(){
        when(repository.getStock("Apple")).thenReturn(5);
        boolean result = orderService.placeOrder("Apple",7);
        Assert.assertFalse(result);
        verify(repository).getStock("Apple");
    }

    @Test
    public void validOrderWithSameStock(){
        when(repository.getStock("Apple")).thenReturn(7);
        boolean result = orderService.placeOrder("Apple",7);
        Assert.assertTrue(result);
        verify(repository).getStock("Apple");
    }

    @Test
    public void invalidStock(){
        Assert.expectThrows(
                IllegalArgumentException.class, ()->
            orderService.placeOrder("Apple",0)
            );
        verifyNoInteractions(repository);
    }

    @Test
    public void stockNotAvailable(){
        when(repository.getStock("Apple")).thenThrow(new RuntimeException("Item not found in inventory"));
        Assert.expectThrows(RuntimeException.class,()->orderService.placeOrder("Apple",10));
        verify(repository).getStock("Apple");
    }

    @Test
    public void invalidItem(){
        Assert.expectThrows(
                IllegalArgumentException.class, ()->
                        orderService.placeOrder("    ",0)
        );
        verifyNoInteractions(repository);
    }

    @Test
    public void nullItem(){
        Assert.expectThrows(
                IllegalArgumentException.class, ()->
                        orderService.placeOrder(null,0)
        );
        verifyNoInteractions(repository);
    }
}
