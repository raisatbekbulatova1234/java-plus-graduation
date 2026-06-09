package ru.practicum.explorewithme.main.service.params;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * ПАРАМЕТРЫ ПОИСКА СОБЫТИЙ ДЛЯ ПУБЛИЧНОГО API
 * ============================================================================
 *
 * Используется в PublicEventController для передачи фильтров в сервисный слой.
 *
 * Поля:
 * - text          - поиск по аннотации и описанию (частичное совпадение)
 * - categories    - список ID категорий для фильтрации
 * - paid          - фильтр по платности (true - платные, false - бесплатные)
 * - rangeStart    - дата начала поиска (не раньше)
 * - rangeEnd      - дата конца поиска (не позже)
 * - onlyAvailable - только события с доступными местами
 * - sort          - тип сортировки: EVENT_DATE (по дате) или VIEWS (по просмотрам)
 */
@Getter
@Builder
@EqualsAndHashCode
public class PublicEventSearchParams {
    private final String text;
    private final List<Long> categories;
    private final Boolean paid;
    private final LocalDateTime rangeStart;
    private final LocalDateTime rangeEnd;
    private final boolean onlyAvailable;
    private final String sort;
}