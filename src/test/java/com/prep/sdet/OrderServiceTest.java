package com.prep.sdet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OrderServiceTest {
    @Mock private OrderRepository repository;
    @Mock private PaymentGateway payments;
    @Mock private Notifier notifier;

    private AutoCloseable mocks;
    private OrderService service;

    @BeforeMethod
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new OrderService(repository, payments, notifier);
        when(repository.nextId()).thenReturn("ORD-1");   // stub: canned answer
    }

    @AfterMethod
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void placeOrder_success_chargesSavesThenNotifies() {
        List<Item> items = List.of(new Item("A", 2, 50.0), new Item("B", 1, 25.0));

        Order order = service.placeOrder("c1", items);

        assertEquals(order.total(), 125.0, 0.001);
        assertEquals(order.status(), OrderStatus.PAID);
        InOrder calls = inOrder(payments, repository, notifier);
        calls.verify(payments).charge("c1", 125.0);
        calls.verify(repository).save(order);
        calls.verify(notifier).send(eq("c1"), contains("ORD-1"));
    }

    @Test
    public void placeOrder_savesOrderWithExpectedFields_usingCaptor() {
        service.placeOrder("c1", List.of(new Item("A", 1, 10.0)));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(repository).save(captor.capture());
        assertEquals(captor.getValue().customerId(), "c1");
        assertEquals(captor.getValue().id(), "ORD-1");
    }

    @Test
    public void placeOrder_paymentFails_nothingSavedAndNobodyNotified() {
        doThrow(new PaymentException("declined")).when(payments).charge(any(), org.mockito.ArgumentMatchers.anyDouble());

        assertThrows(PaymentException.class,
                () -> service.placeOrder("c1", List.of(new Item("A", 1, 10.0))));

        verify(repository, never()).save(any());
        verifyNoInteractions(notifier);
    }

    @Test
    public void placeOrder_notifierFails_orderStillSucceeds() {
        doThrow(new RuntimeException("smtp down")).when(notifier).send(any(), any());

        Order order = service.placeOrder("c1", List.of(new Item("A", 1, 10.0)));

        assertEquals(order.status(), OrderStatus.PAID);
        verify(repository).save(order);
    }

    @Test
    public void placeOrder_emptyItems_throwsAndTouchesNothing() {
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("c1", List.of()));
        verifyNoInteractions(payments, notifier);
    }

    @Test
    public void cancelOrder_paidOrder_refundsAndSavesCancelled() {
        Order paid = new Order("ORD-1", "c1", 100.0, OrderStatus.PAID);
        when(repository.findById("ORD-1")).thenReturn(Optional.of(paid));

        Order result = service.cancelOrder("ORD-1");

        assertEquals(result.status(), OrderStatus.CANCELLED);
        verify(payments).refund("c1", 100.0);
    }

    @Test
    public void cancelOrder_shippedOrder_throwsAndDoesNotRefund() {
        when(repository.findById("ORD-1"))
                .thenReturn(Optional.of(new Order("ORD-1", "c1", 100.0, OrderStatus.SHIPPED)));

        assertThrows(IllegalStateException.class, () -> service.cancelOrder("ORD-1"));
        verify(payments, never()).refund(any(), org.mockito.ArgumentMatchers.anyDouble());
    }

    @Test
    public void cancelOrder_unknownId_throwsNoSuchElement() {
        when(repository.findById("X")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.cancelOrder("X"));
    }
}
