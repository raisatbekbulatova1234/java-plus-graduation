package ru.practicum.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static ru.practicum.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;
/**
 * ============================================================================
 * DTO ДЛЯ КОММЕНТАРИЯ (ПОЛЬЗОВАТЕЛЬСКИЙ API)
 * ============================================================================
 *
 * Используется для отображения комментария обычным пользователям.
 *
 * Отличия от CommentAdminDto:
 * - НЕ содержит поле isDeleted (обычные пользователи не видят удалённые комментарии)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    Long id;

    String text;

    UserShortDto author;

    Long eventId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime updatedOn;

    Boolean isEdited;
}