package ru.practicum.explorewithme.main.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
/**
 * ============================================================================
 * ПОЛНЫЙ DTO ДЛЯ ПОЛЬЗОВАТЕЛЯ (User)
 * ============================================================================
 *
 * Используется для передачи полной информации о пользователе.
 * Применяется:
 * - Административный API (список пользователей, создание/удаление)
 * - При необходимости показать email пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;

    String name;

    String email;
}