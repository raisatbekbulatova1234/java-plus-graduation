package ru.practicum.stats.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * ============================================================================
 * DTO ДЛЯ РЕЗУЛЬТАТА СТАТИСТИКИ (VIEW STATS)
 * ============================================================================
 *
 * Используется для возврата агрегированной статистики по обращениям.
 * Возвращается методом GET /stats.
 *
 * Поля:
 * - app  - название сервиса-источника
 * - uri  - URI запроса
 * - hits - количество обращений (общее или уникальное, в зависимости от параметра unique)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStatsDto {
    String app;
    String uri;
    Long hits;
}