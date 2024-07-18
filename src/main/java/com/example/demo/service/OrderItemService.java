package com.example.demo.service;

import com.example.demo.model.OrderItemEntity;
import com.example.demo.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItemEntity> getNotReturnedItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderIdAndReturnedFalse(orderId);
    }
}
