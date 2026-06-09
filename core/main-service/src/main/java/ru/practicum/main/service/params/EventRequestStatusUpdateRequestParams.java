package ru.practicum.main.service.params;

import lombok.*;
import ru.practicum.main.model.RequestStatus;

import java.util.List;

/**
 * ============================================================================
 * ПАРАМЕТРЫ ОБНОВЛЕНИЯ СТАТУСА ЗАЯВОК НА УЧАСТИЕ
 * ============================================================================
 *
 * Используется в PrivateEventController для передачи данных для массового
 * обновления статусов заявок в сервисный слой.
 *
 * Поля:
 * - userId     - ID пользователя (инициатора события)
 * - eventId    - ID события
 * - requestIds - список ID заявок для обновления
 * - status     - новый статус (CONFIRMED или REJECTED)
 */
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class EventRequestStatusUpdateRequestParams {
    private final Long userId;
    private final Long eventId;
    private final List<Long> requestIds;
    private final RequestStatus status;
}