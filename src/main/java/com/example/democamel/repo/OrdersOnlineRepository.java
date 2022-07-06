package com.example.democamel.repo;

import com.example.democamel.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersOnlineRepository extends JpaRepository<OrderEntity, Long> {
}
