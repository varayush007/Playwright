package com.prep.sdet.solutions;

import static com.prep.sdet.OrderStatus.*;
import static org.testng.Assert.*;

import com.prep.sdet.Order;
import com.prep.sdet.OrderStatus;
import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OrderStatusTest {
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            NEW, EnumSet.of(PAID, CANCELLED),
            PAID, EnumSet.of(SHIPPED, CANCELLED),
            SHIPPED, EnumSet.of(DELIVERED),
            DELIVERED, EnumSet.noneOf(OrderStatus.class),
            CANCELLED, EnumSet.noneOf(OrderStatus.class));

    @DataProvider(name = "allPairs")
    public Object[][] allPairs() {
        List<Object[]> rows = new ArrayList<>();
        for (OrderStatus from : values())
            for (OrderStatus to : values())
                rows.add(new Object[]{from, to, ALLOWED.get(from).contains(to)});
        return rows.toArray(new Object[0][]);
    }

    @Test(dataProvider = "allPairs")
    public void canTransitionTo_matchesTransitionTable(OrderStatus from, OrderStatus to, boolean expected) {
        assertEquals(from.canTransitionTo(to), expected, from + " -> " + to);
    }

    @Test
    public void transitionTo_validMove_returnsNewOrderAndKeepsOriginal() {
        Order original = new Order("1", "c1", 10.0, NEW);
        Order paid = original.transitionTo(PAID);
        assertEquals(paid, new Order("1", "c1", 10.0, PAID));
        assertEquals(original.status(), NEW);
    }

    @Test
    public void transitionTo_fullHappyPath() {
        Order o = new Order("1", "c1", 10.0, NEW)
                .transitionTo(PAID).transitionTo(SHIPPED).transitionTo(DELIVERED);
        assertEquals(o.status(), DELIVERED);
    }

    @Test
    public void transitionTo_invalidMove_throwsWithBothStatusesInMessage() {
        Order delivered = new Order("1", "c1", 10.0, DELIVERED);
        IllegalStateException ex = expectThrows(IllegalStateException.class,
                () -> delivered.transitionTo(PAID));
        assertTrue(ex.getMessage().contains("DELIVERED") && ex.getMessage().contains("PAID"));
    }

    @Test
    public void terminalStates_rejectEveryTransition() {
        for (OrderStatus terminal : List.of(DELIVERED, CANCELLED)) {
            Order o = new Order("1", "c1", 10.0, terminal);
            for (OrderStatus next : values()) {
                assertThrows(IllegalStateException.class, () -> o.transitionTo(next));
            }
        }
    }
}
