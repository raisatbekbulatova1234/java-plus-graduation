package ru.practicum.explorewithme.main.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * ============================================================================
 * DTO ДЛЯ КАТЕГОРИИ СОБЫТИЙ (Category)
 * ============================================================================
 * Используется для передачи информации о категории событий.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto {

    Long id;

    String name;

}