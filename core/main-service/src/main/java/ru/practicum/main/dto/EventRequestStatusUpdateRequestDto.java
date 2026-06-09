package ru.practicum.main.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.model.RequestStatus;

import java.util.List;
/**
 * ============================================================================
 * DTO ДЛЯ МАССОВОГО ОБНОВЛЕНИЯ СТАТУСОВ ЗАПРОСОВ НА УЧАСТИЕ
 * ============================================================================
 *
 * Используется инициатором события для массового подтверждения или отклонения
 * заявок на участие в событии.
 *
 * Пример запроса:
 * PATCH /users/{userId}/events/{eventId}/requests
 * {
 *     "requestIds": [1, 2, 3, 5],
 *     "status": "CONFIRMED"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequestDto {

    @NotEmpty
    List<Long> requestIds;

    @NotNull
    RequestStatus status;

}
