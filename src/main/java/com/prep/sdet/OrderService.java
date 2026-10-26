package com.prep.sdet;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Snippet 6: service with injected dependencies.
 * placeOrder: validates input, charges the total, saves a PAID order, notifies the customer.
 *   - Invalid input throws IAE before anything else is touched.
 *   - If the charge fails nothing is saved and nobody is notified.
 *   - A notification failure must not fail the order.
 * cancelOrder: unknown id throws NoSuchElementException; illegal transition throws IllegalStateException;
 *   refunds only if the order had been PAID.
 */
public class OrderService {
    private final OrderRepository repository;
    private final PaymentGateway paymentGateway;
    private final Notifier notifier;

    public OrderService(OrderRepository repository, PaymentGateway paymentGateway, Notifier notifier) {
        this.repository = repository;
        this.paymentGateway = paymentGateway;
        this.notifier = notifier;
    }

    public Order placeOrder(String customerId, List<Item> items) {
        if (customerId == null || customerId.isBlank()) throw new IllegalArgumentException("customerId required");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("items required");
        double total = 0;
        for (Item i : items) {
            if (i.qty() <= 0 || i.price() < 0) throw new IllegalArgumentException("invalid item " + i.sku());
            total += i.qty() * i.price();
        }
        paymentGateway.charge(customerId, total);
        Order order = new Order(repository.nextId(), customerId, total, OrderStatus.PAID);
        repository.save(order);
        try {
            notifier.send(customerId, "Order " + order.id() + " confirmed");
        } catch (RuntimeException ignored) {
            // notification failure must not fail the order
        }
        return order;
    }

    public Order cancelOrder(String orderId) {
        Order existing = repository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("order " + orderId));
        Order cancelled = existing.transitionTo(OrderStatus.CANCELLED);
        if (existing.status() == OrderStatus.PAID) {
            paymentGateway.refund(existing.customerId(), existing.total());
        }
        repository.save(cancelled);
        return cancelled;
    }
}
