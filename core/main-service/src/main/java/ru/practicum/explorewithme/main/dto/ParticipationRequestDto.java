package ru.practicum.explorewithme.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.main.model.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.explorewithme.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;
/**
 * ============================================================================
 * DTO ДЛЯ ЗАПРОСА НА УЧАСТИЕ В СОБЫТИИ (Participation Request)
 * ============================================================================
 *
 * Используется для отображения информации о запросе пользователя на участие в событии.
 *
 * Содержит:
 * - ID запроса
 * - Дата создания запроса
 * - ID пользователя (requester)
 * - ID события (event)
 * - Статус запроса (PENDING, CONFIRMED, REJECTED, CANCELED)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {

    Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime created;

    @JsonProperty("requester")
    Long requesterId;

    @JsonProperty("event")
    Long eventId;

    RequestStatus status;

}
