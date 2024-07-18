package com.example.demo.model;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
public class OrderItemEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(nullable = false, unique = true)
    private Long id;

    @ManyToOne(optional = false)
    private OrderEntity order;

    @Column(nullable = false)
    private Long productId;

    @Builder.Default
    private boolean returned = false;

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
        OrderItemEntity that = (OrderItemEntity) o;
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
Использование CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH в @ManyToOne отношении:
Это может привести к нежелательным каскадным операциям на OrderEntity при выполнении операций с OrderItemEntity.
Например, сохранение OrderItemEntity может неожиданно изменить состояние связанного OrderEntity в базе данных.
Решение: Пересмотреть необходимость каскадных операций для данного отношения. Если каскадные операции не требуются, их можно убрать,
оставив только @ManyToOne(optional = false).

Отсутствие @Table аннотации с явным указанием имени таблицы:
Хотя это не является ошибкой, явное указание имени таблицы может помочь избежать потенциальных проблем с именованием,
особенно при работе с различными базами данных.
Решение: Добавить аннотацию @Table с явным указанием имени таблицы.

Использование примитивного типа для поля returned с аннотацией @Builder.Default:
В данном контексте это не ошибка, но использование обертки Boolean может предоставить больше гибкости,
например, позволяя полю быть null в некоторых сценариях.
Решение: Если требуется возможность установки поля в null, использовать Boolean вместо boolean.

Отсутствие индексов на часто используемые поля:
Если поля order и productId часто используются для поиска, отсутствие индексов может снизить производительность запросов.
Решение: Добавить аннотацию @Index в аннотацию @Table для создания индексов на эти поля.
*/
