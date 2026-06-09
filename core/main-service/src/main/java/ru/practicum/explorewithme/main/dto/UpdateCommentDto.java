package ru.practicum.explorewithme.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * ============================================================================
 * DTO ДЛЯ ОБНОВЛЕНИЯ КОММЕНТАРИЯ
 * ============================================================================
 *
 * Используется пользователем для редактирования текста своего комментария.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommentDto {

    /**
     * Обновлённый текст комментария.
     */
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 1, max = 2000, message = "Текст комментария должен быть от 1 до 2000 символов")
    String text;
}