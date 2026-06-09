package ru.practicum.explorewithme.main.dto;

import static ru.practicum.explorewithme.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.main.model.Location;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * DTO ДЛЯ ОБНОВЛЕНИЯ СОБЫТИЯ ПОЛЬЗОВАТЕЛЕМ
 * ============================================================================
 *
 * Используется пользователем для редактирования своего события.
 * Все поля опциональны (обновляются только переданные значения).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequestDto {

    /**
     * Краткая аннотация события.
     */
    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов")
    String annotation;

    /**
     * ID категории события (можно изменить).
     */
    Long category;

    /**
     * Полное описание события.
     */
    @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")
    String description;

    /**
     * Дата и время проведения события.
     * Должна быть в будущем.
     * Формат: согласно DATE_TIME_FORMAT_PATTERN.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    @Future(message = "Дата события должна быть в будущем")
    LocalDateTime eventDate;

    /**
     * Местоположение события (широта и долгота).
     */
    Location location;

    /**
     * Флаг платного участия.
     * true - платное, false - бесплатное.
     */
    Boolean paid;

    /**
     * Лимит участников события.
     */
    @PositiveOrZero(message = "Лимит участников должен быть положительным или нулём")
    Integer participantLimit;

    /**
     * Требуется ли модерация заявок на участие.
     */
    Boolean requestModeration;

    StateActionUser stateAction;

    /**
     * Заголовок события.
     */
    @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 3 до 120 символов")
    String title;

    /**
     * Перечисление возможных действий пользователя с событием.
     */
    public enum StateActionUser {
        SEND_TO_REVIEW,   // Отправить на модерацию
        CANCEL_REVIEW     // Отменить модерацию
    }
}