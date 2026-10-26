package com.prep.sdet;

/**
 * Snippet 7: state machine.
 * NEW -> PAID | CANCELLED; PAID -> SHIPPED | CANCELLED; SHIPPED -> DELIVERED.
 * DELIVERED and CANCELLED are terminal.
 */
public enum OrderStatus {
    NEW, PAID, SHIPPED, DELIVERED, CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case NEW -> next == PAID || next == CANCELLED;
            case PAID -> next == SHIPPED || next == CANCELLED;
            case SHIPPED -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
