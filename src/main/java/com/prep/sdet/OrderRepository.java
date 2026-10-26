package com.prep.sdet;

import java.util.Optional;

public interface OrderRepository {
    String nextId();
    void save(Order order);
    Optional<Order> findById(String id);
}
