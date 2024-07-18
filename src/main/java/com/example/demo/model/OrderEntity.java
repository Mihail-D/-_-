package com.example.demo.model;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class OrderEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(nullable = false, unique = true)
    private Long id;

    @Builder.Default
    private boolean issued = false;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            mappedBy = "order",
            fetch = FetchType.LAZY)
    private Set<OrderItemEntity> orderItems;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        OrderEntity that = (OrderEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

/*
Использование @EqualsAndHashCode для объектов JPA не рекомендуется. Это может вызвать серьезные проблемы с производительностью и
потреблением памяти.*/

/*
Использование FetchType.EAGER для коллекции orderItems:
Это может привести к проблемам производительности, так как при загрузке OrderEntity будут загружаться все связанные OrderItemEntity,
даже если они не нужны.
Лучше использовать FetchType.LAZY для ленивой загрузки.
Решение: Изменить fetch = FetchType.EAGER на fetch = FetchType.LAZY.

Отсутствие @Transactional аннотации на методах, изменяющих состояние:
В случае, если класс OrderEntity используется в контексте Spring и предполагается изменение данных,
отсутствие @Transactional может привести к некорректному поведению при выполнении операций с базой данных.
Решение: Хотя непосредственно в классе сущности аннотация @Transactional не используется, следует убедиться, что сервисы или репозитории,
работающие с этим классом, используют @Transactional при необходимости.

Использование CascadeType.ALL:
Это может привести к нежелательным каскадным операциям, например, удалению всех связанных OrderItemEntity при удалении OrderEntity.
В зависимости от бизнес-логики, может быть предпочтительнее использовать более ограниченный набор каскадных типов.
Решение: Пересмотреть и, возможно, ограничить каскадные операции, используя более специфичные типы,
например, CascadeType.PERSIST и CascadeType.MERGE.

Отсутствие валидации данных:
В классе не реализована валидация для полей, что может привести к добавлению в систему некорректных данных.
Решение: Добавить валидацию данных, например, с использованием аннотаций из библиотеки Bean Validation*/
