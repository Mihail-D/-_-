package com.example.demo.service;

import com.example.demo.model.OrderEntity;
import com.example.demo.model.OrderItemEntity;
import com.example.demo.repository.OrderDto;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EntityManager entityManager;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            EntityManager entityManager
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.entityManager = entityManager;
    }

/*  Отсутствие валидации входных данных в createOrder:
    Проблема: В методе createOrder проверяется только пустота списка productIds, но не проверяется, что идентификаторы
    продуктов действительны (например, неотрицательные).
    Решение: Добавить дополнительные проверки для идентификаторов продуктов, чтобы убедиться в их корректности.*/
    public void createOrder(List<Long> productIds) {
        if (ObjectUtils.isEmpty(productIds) || productIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new IllegalArgumentException("Product ids must be non-null and positive");
        }
        var orderItems = productIds.stream()
                .map(id -> OrderItemEntity.builder()
                        .productId(id)
                        .build())
                .collect(Collectors.toSet());
        createOrderFromItems(orderItems);
    }

/*    Неправильное использование транзакций:
    Проблема: Методы createOrderFromItems и returnOrder используют разные уровни изоляции транзакций без явной необходимости. Это может привести к
    непредсказуемому поведению и проблемам с производительностью.
    Решение: Убедиться, что уровни изоляции транзакций соответствуют требованиям бизнес-логики. Если нет специфических требований, использовать
    уровень изоляции по умолчанию.
    Отсутствие проверки на null в createOrderFromItems:
    Проблема: В методе createOrderFromItems не производится проверка передаваемого параметра orderItems на null,
    что может привести к NullPointerException.
    Решение: Добавить проверку на null и выбросить исключение, если параметр null.*/

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createOrderFromItems(Set<OrderItemEntity> orderItems) {
        if (orderItems == null) {
            throw new IllegalArgumentException("Order items cannot be null");
        }

        var order = OrderEntity.builder()
                .orderItems(orderItems)
                .build();
        entityManager.persist(order);
        publishOrderCreation(order);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Long> returnOrder(
            Long orderId,
            Long returnedProductId
    ) {
        if (returnedProductId == null) {
            throw new IllegalArgumentException("Product id can not be null");
        }
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.isIssued()) {
            throw new IllegalArgumentException("Order was already issued");
        }
        for (var orderItem : order.getOrderItems()) {
            if (orderItem.getProductId().equals(returnedProductId)) {
                if (orderItem.isReturned()) {
                    throw new IllegalArgumentException("Product already returned");
                }
                orderItem.setReturned(true);
                publishOrderReturn(orderId, returnedProductId);
                return orderItemRepository.getNotReturnedProductIds(orderId);
            }
        }
        throw new IllegalArgumentException("Product not found in order");
    }

    /*Неправильное использование EntityManager.persist для обновления сущности:
        Проблема: В методе issueOrder, после изменения состояния сущности OrderEntity,
        используется entityManager.persist(order) для сохранения изменений.
        Однако, persist предназначен для сохранения новых сущностей, а не для обновления существующих.
        Решение: Использовать entityManager.merge(order) для обновления существующей сущности или полагаться на автоматическое
        применение изменений при управлении сущностью в контексте транзакции.*/
    @Transactional
    public void issueOrder(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.isIssued()) {
            throw new IllegalArgumentException("Order was already issued");
        }
        order.setIssued(true);
        entityManager.merge(order);
    }

/*    Использование parallelStream() в getAllOrders():
    Проблема: Использование parallelStream() может быть неэффективным для небольших коллекций или операций, которые уже являются интенсивными по
    использованию ресурсов, так как оверхед на параллелизацию может превысить выгоду от неё. Кроме того, это может привести к
    непредсказуемому поведению в контексте транзакций.
    Решение: Использовать обычный stream() вместо parallelStream().*/

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    var productIds = order.getOrderItems().stream()
                            .filter(orderItem -> !orderItem.isReturned())
                            .map(OrderItemEntity::getProductId)
                            .collect(Collectors.toList());
                    return new OrderDto(order.getId(), productIds);
                })
                .collect(Collectors.toList());
    }

    private void publishOrderCreation(OrderEntity order) {
        //Внутри метода происходит отправка данных о создания заказа в платежную систему
    }

    private void publishOrderReturn(
            long orderId,
            long productId
    ) {
        //Внутри метода происходит запрос на возврат средств по товару в заказе
    }
}
