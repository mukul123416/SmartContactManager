package com.example.demo.repositories;

import com.example.demo.entities.MyOrder;
import com.example.demo.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyOrderRepository extends JpaRepository<MyOrder,Long> {

    public MyOrder findByOrderId(String orderId);

    Page<MyOrder> findAll(Pageable pageable);

    public List<MyOrder> findByOrderIdContaining(String keyword);
}
