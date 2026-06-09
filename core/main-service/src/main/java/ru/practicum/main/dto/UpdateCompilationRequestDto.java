package ru.practicum.main.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * ============================================================================
 * DTO ДЛЯ ОБНОВЛЕНИЯ ПОДБОРКИ СОБЫТИЙ (Compilation)
 * ============================================================================
 * <p>
 * Используется администратором для частичного обновления подборки.
 * Все поля опциональны (обновляются только переданные значения).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequestDto {
    /**
     * Флаг закрепления подборки на главной странице.
     */
    Boolean pinned;

    /**
     * Название подборки.
     */
    @Size(min = 1, max = 50, message = "Название подборки должно быть от 1 до 50 символов")
    String title;

    /**
     * Список ID событий, входящих в подборку.
     */
    List<Long> events;
}