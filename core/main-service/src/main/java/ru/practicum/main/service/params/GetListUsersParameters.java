package ru.practicum.main.service.params;

import lombok.*;

import java.util.List;

/**
 * ============================================================================
 * ПАРАМЕТРЫ ПОЛУЧЕНИЯ СПИСКА ПОЛЬЗОВАТЕЛЕЙ
 * ============================================================================
 *
 * Используется в AdminUserController для передачи параметров фильтрации
 * и пагинации в сервисный слой.
 *
 * Поля:
 * - ids  - список ID пользователей для фильтрации (опционально)
 * - from - количество пропускаемых элементов (пагинация)
 * - size - размер страницы (количество элементов)
 */
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class GetListUsersParameters {
    private final List<Long> ids;
    private final int from;
    private final int size;
}