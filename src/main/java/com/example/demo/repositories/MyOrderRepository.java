package com.example.demo.repositories;

import com.example.demo.entities.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyOrderRepository extends JpaRepository<MyOrder,Long> {

    public MyOrder findByOrderId(String orderId);

}
