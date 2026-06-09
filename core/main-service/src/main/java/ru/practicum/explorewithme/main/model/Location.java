package ru.practicum.explorewithme.main.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * ============================================================================
 * ВСТРОЕННЫЙ ОБЪЕКТ "МЕСТОПОЛОЖЕНИЕ" (LOCATION)
 * ============================================================================
 * Представляет географические координаты места проведения события.
 * Используется как встроенное значение (Embedded) в сущности Event.
 * ============================================================================
 */
@Embeddable   // Указывает, что объект можно встраивать в другие сущности
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Location {

    /**
     * Широта географической точки.
     */
    @Column(name = "lat", nullable = false)
    private Float lat;

    /**
     * Долгота географической точки.
     */
    @Column(name = "lon", nullable = false)
    private Float lon;
}