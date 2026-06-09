package ru.practicum.explorewithme.main.service.params;

import lombok.*;
import org.springframework.data.domain.Sort;

/**
 * ============================================================================
 * ПАРАМЕТРЫ ПОЛУЧЕНИЯ КОММЕНТАРИЕВ ДЛЯ ПУБЛИЧНОГО API
 * ============================================================================
 *
 * Используется в PublicCommentController для передачи параметров пагинации
 * и сортировки в сервисный слой.
 *
 * Поля:
 * - from - количество пропускаемых элементов (пагинация)
 * - size - размер страницы (количество элементов)
 * - sort - правила сортировки (по дате создания ASC/DESC)
 */
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
public class PublicCommentParameters {
    private final int from;
    private final int size;
    private final Sort sort;
}