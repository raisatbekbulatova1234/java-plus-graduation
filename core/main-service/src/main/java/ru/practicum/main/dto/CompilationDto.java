package ru.practicum.main.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
/**
 * ============================================================================
 * DTO ДЛЯ ПОДБОРКИ СОБЫТИЙ (Compilation)
 * ============================================================================
 *
 * Используется для отображения подборки событий:
 * - Главная страница (закреплённые подборки)
 * - Список подборок
 * - Детальная страница подборки
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    Long id;

    Boolean pinned;

    String title;

    Set<EventShortDto> events;
}
