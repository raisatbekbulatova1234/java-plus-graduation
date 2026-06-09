package ru.practicum.explorewithme.main.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
/**
 * ============================================================================
 * СОКРАЩЁННЫЙ DTO ДЛЯ ПОЛЬЗОВАТЕЛЯ (User Short)
 * ============================================================================
 *
 * НЕ СОДЕРЖИТ поле email (конфиденциальная информация)
 *
 * Применяется:
 * - Автор комментария (CommentDto.author)
 * - Инициатор события (EventShortDto.initiator, EventFullDto.initiator)
 * - Любые другие случаи, когда нужно показать автора/создателя
 *   без раскрытия личных данных
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserShortDto {

    Long id;

    String name;
}