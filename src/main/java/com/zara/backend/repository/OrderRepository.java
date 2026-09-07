package com.zara.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zara.backend.entity.Order;
import com.zara.backend.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /*
     * Fetch all orders together with their User.
     *
     * This prevents LazyInitializationException when
     * OrderResponse accesses user information.
     */
    @EntityGraph(attributePaths = "user")
    @Query("""
            SELECT o
            FROM Order o
            ORDER BY o.createdAt DESC
            """)
    List<Order> findAllOrdersWithUser();

    /*
     * Fetch a single order together with its User.
     */
    @EntityGraph(attributePaths = "user")
    @Query("""
            SELECT o
            FROM Order o
            WHERE o.id = :id
            """)
    Optional<Order> findOrderWithUser(@Param("id") Long id);

    /*
     * Fetch customer's orders with User information.
     */
    @EntityGraph(attributePaths = "user")
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}