package com.prep.sdet;

/** Snippet 7 (state machine) lives in Order + OrderStatus. */
public record Order(String id, String customerId, double total, OrderStatus status) {
    public Order transitionTo(OrderStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new IllegalStateException(status + " -> " + next + " is not allowed");
        }
        return new Order(id, customerId, total, next);
    }
}
