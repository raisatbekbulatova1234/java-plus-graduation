package ru.practicum.common.error;

import static ru.practicum.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * API ОШИБКА (ApiError)
 * ============================================================================
 *
 * Стандартный формат ответа при возникновении ошибок в API.
 * Используется во всех сервисах проекта (main-service, stats-server, gateway).
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  // Не включает null-поля в JSON
public class ApiError {
    private HttpStatus status;               // HTTP статус (BAD_REQUEST, NOT_FOUND, etc.)
    private String reason;                   // Причина ошибки
    private String message;                  // Сообщение об ошибке

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    private LocalDateTime timestamp;         // Время возникновения

    private List<String> errors;             // Детальный список ошибок (для валидации)
}