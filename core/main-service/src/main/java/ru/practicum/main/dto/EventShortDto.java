package ru.practicum.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static ru.practicum.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;
/**
 * ============================================================================
 * СОКРАЩЁННЫЙ DTO ДЛЯ СОБЫТИЯ (Event Short)
 * ============================================================================
 *
 * Используется для отображения события в списках:
 * - Главная страница
 * - Результаты поиска
 * - Подборки событий
 * - Список событий пользователя
 *
 * Содержит только основную информацию о событии без детального описания.
 * Поля views и confirmedRequests заполняются из сервиса статистики.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    Long id;

    String annotation;

    CategoryDto category;

    Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime eventDate;

    UserShortDto initiator;

    Boolean paid;

    String title;

    Long views;
}