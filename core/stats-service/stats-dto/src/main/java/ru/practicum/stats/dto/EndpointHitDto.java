package ru.practicum.stats.dto;

import static ru.practicum.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * DTO ДЛЯ ИНФОРМАЦИИ ОБ ОБРАЩЕНИИ К ЭНДПОИНТУ
 * ============================================================================
 *
 * Используется для передачи данных между клиентом и сервером статистики.
 *
 * Поля:
 * - app       - название сервиса-источника (не пустое, длина 1-32 символа)
 * - uri       - URI запроса (не пустое, длина 1-128 символов)
 * - ip        - IP-адрес клиента (не пустое, длина 7-16 символов)
 * - timestamp - дата и время обращения (не null, не позже текущего момента)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitDto {

    @NotBlank(message = "Поле app не может быть пустым")
    @Size(min = 1, max = 32, message = "Поле app должно быть от 1 до 32 символов")
    String app;

    @NotBlank(message = "Поле uri не может быть пустым")
    @Size(min = 1, max = 128, message = "Поле uri должно быть от 1 до 128 символов")
    String uri;

    @NotBlank(message = "Поле ip не может быть пустым")
    @Size(min = 7, max = 16, message = "Поле ip должно быть от 7 до 16 символов")
    String ip;

    @NotNull(message = "Поле timestamp не может быть пустым")
    @PastOrPresent(message = "Поле timestamp должно быть не позже текущей даты и времени")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT_PATTERN)
    LocalDateTime timestamp;
}