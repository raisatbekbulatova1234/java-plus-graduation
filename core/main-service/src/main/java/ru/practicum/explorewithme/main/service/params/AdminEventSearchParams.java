package ru.practicum.explorewithme.main.service.params;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.practicum.explorewithme.main.model.EventState;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * ПАРАМЕТРЫ ПОИСКА СОБЫТИЙ ДЛЯ АДМИНИСТРАТОРА
 * ============================================================================
 *
 * Используется в AdminEventController для передачи фильтров в сервисный слой.
 *
 * Поля:
 * - users       - список ID пользователей (авторов событий)
 * - states      - список статусов событий (PENDING, PUBLISHED, CANCELED)
 * - categories  - список ID категорий
 * - rangeStart  - дата начала поиска (не раньше)
 * - rangeEnd    - дата конца поиска (не позже)
 */
@Getter
@Builder
@EqualsAndHashCode(of = {"users", "states", "categories", "rangeStart", "rangeEnd"})
public class AdminEventSearchParams {

    private final List<Long> users;

    private final List<EventState> states;

    private final List<Long> categories;

    private final LocalDateTime rangeStart;

    private final LocalDateTime rangeEnd;
}