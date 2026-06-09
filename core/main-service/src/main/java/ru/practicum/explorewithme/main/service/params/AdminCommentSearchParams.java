package ru.practicum.explorewithme.main.service.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * ============================================================================
 * ПАРАМЕТРЫ ПОИСКА КОММЕНТАРИЕВ ДЛЯ АДМИНИСТРАТОРА
 * ============================================================================
 *
 * Используется в AdminCommentController для передачи фильтров в сервисный слой.
 *
 * Поля:
 * - userId    - ID автора комментария (опционально)
 * - eventId   - ID события (опционально)
 * - isDeleted - фильтр по удалению: true - удалённые, false - активные, null - все
 */
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class AdminCommentSearchParams {

    private final Long userId;

    private final Long eventId;

    private final Boolean isDeleted;
}