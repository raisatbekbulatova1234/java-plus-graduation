package ru.practicum.explorewithme.main.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * ============================================================================
 * СУЩНОСТЬ "КАТЕГОРИЯ" (CATEGORY)
 * ============================================================================
 * Представляет категорию событий в системе Explore With Me.
 * Категории используются для группировки событий по тематике:
 * - Концерты
 * - Спектакли
 * - Выставки
 * - Мастер-классы
 * - Спортивные мероприятия
 * и т.д.
 * ============================================================================
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"id", "name"})
public class Category {

    /**
     * Уникальный идентификатор категории.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Наименование категории.
     */
    @Column(name = "name",
            nullable = false,    // NOT NULL
            length = 50,         // VARCHAR(50)
            unique = true)       // UNIQUE - уникальное значение
    private String name;
}