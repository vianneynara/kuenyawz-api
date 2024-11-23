package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.orderItems.OrderItemDto;
import dev.realtards.kuenyawz.dtos.orderItems.OrderItemPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderItemsService {

    @Transactional(readOnly = true)
    List<OrderItemDto> getAllOrderItems();

    @Transactional(readOnly = true)
    Page<OrderItemDto> getAllOrderItems(PageRequest pageRequest);

    @Transactional(readOnly = true)
    OrderItemDto getOrderItemById(Long id);

    @Transactional(readOnly = true)
    List<OrderItemDto> getOrderItemsOfAccount(Long accountId);

    @Transactional(readOnly = true)
    Page<OrderItemDto> getOrderItemsOfAccount(PageRequest pageRequest, Long accountId);

    @Transactional
    OrderItemDto createOrderItem(Long accountId, OrderItemPostDto orderItemPostDto);

    @Transactional
    void deleteOrderItem(Long orderItemId);

    @Transactional
    boolean deleteOrderItemsOfAccount(Long accountId, Long orderId);
}
