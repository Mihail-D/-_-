package com.example.demo.repository;

import com.example.demo.model.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    @Query(nativeQuery = true, value = "" +
            "SELECT product_id FROM order_item_entity " +
            "WHERE order_id = ?1 " +
            "AND returned = 'false'")
    List<OrderItemEntity> findByOrderIdAndReturnedFalse(Long orderId);
}

/*Неправильный тип сущности для репозитория: OrderItemRepository расширяет JpaRepository<OrderEntity, Long>, что указывает на то,
что репозиторий предназначен для работы с сущностями OrderEntity, в то время как из названия и методов репозитория следует,
что он должен работать с сущностями OrderItemEntity.
Это может привести к путанице и ошибкам во время выполнения, так как тип сущности не соответствует предполагаемому использованию репозитория.
Решение: Изменить тип сущности, с которой работает репозиторий, на OrderItemEntity.
*/

/*
Использование нативного SQL-запроса для простой операции:
В методе getNotReturnedProductIds используется нативный SQL-запрос для выборки идентификаторов продуктов,
которые не были возвращены.
Хотя это не ошибка в строгом смысле, использование нативных запросов может усложнить поддержку кода и уменьшить переносимость
между различными базами данных.
Spring Data JPA предоставляет мощные средства для формирования запросов на основе методов, что может упростить код и
сделать его более устойчивым к изменениям в модели данных.
Решение: Использовать возможности Spring Data JPA для формирования запроса через имя метода.*/
