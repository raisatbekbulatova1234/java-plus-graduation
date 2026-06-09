package ru.practicum.main.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * DTO РЕЗУЛЬТАТА ОБНОВЛЕНИЯ СТАТУСОВ ЗАПРОСОВ НА УЧАСТИЕ
 * ============================================================================
 *
 * Используется для возврата результатов массового обновления статусов заявок
 * инициатором события (подтверждение/отклонение).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResultDto {

    /**
     * Список подтверждённых запросов на участие.
     * Содержит DTO запросов, статус которых изменён на CONFIRMED.
     */
    @Builder.Default
    List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    /**
     * Список отклонённых запросов на участие.
     * Содержит DTO запросов, статус которых изменён на REJECTED.
     */
    @Builder.Default
    List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

}