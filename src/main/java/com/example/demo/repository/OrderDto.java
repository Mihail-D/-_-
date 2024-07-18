package com.example.demo.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @NotNull
    private Long orderId;

    @NotEmpty
    private Set<@NotNull Long> productIds;
}

/*Использование примитивного типа Long для orderId:
Использование обертки Long для orderId может быть неоптимальным, если orderId не может быть null.
В случае, если orderId всегда должен иметь значение, использование примитивного типа long может быть более предпочтительным
для избежания ненужной обертки и потенциальных NullPointerException.
Решение: Изменить тип orderId на примитивный long, если это допустимо по логике приложения.
Отсутствие валидации данных:
В классе не реализована валидация для orderId и productIds, что может привести к добавлению в систему некорректных данных.
Решение: Добавить валидацию данных, например, с использованием аннотаций из библиотеки Bean Validation.
Неэффективное использование коллекций:
Если предполагается, что идентификаторы продуктов в productIds должны быть уникальными, использование List может быть не лучшим выбором,
так как оно позволяет дублирование элементов.
Решение: Использовать Set<Long> вместо List<Long> для productIds, чтобы обеспечить уникальность идентификаторов продуктов.*/
