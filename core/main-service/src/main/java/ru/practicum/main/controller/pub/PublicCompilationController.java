package ru.practicum.main.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.service.CompilationService;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * ============================================================================
 * ПУБЛИЧНЫЙ КОНТРОЛЛЕР ПОДБОРОК
 * ============================================================================
 *
 * Обрабатывает запросы от неавторизованных пользователей для просмотра подборок.
 * Доступен без аутентификации.
 */
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCompilationController {

    private final CompilationService compilationService;

    /**
     * Получение списка подборок с фильтрацией по закреплённым на главной.
     * Параметры:
     * - pinned - фильтр по закреплённым (true/false), если null - все подборки
     * - from   - количество пропускаемых элементов (по умолчанию 0)
     * - size   - размер страницы (по умолчанию 10)
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(
            @RequestParam(name = "pinned", required = false) Boolean pinned,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Публичный: Получен запрос на получение подборок с параметрами pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> result = compilationService.getCompilations(pinned, from, size);
        log.info("Публичный: Найдено подборок: {}", result.size());
        return result;
    }

    /**
     * Получение подборки по ID.
     */
    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable @Positive Long compId) {
        log.info("Публичный: Получен запрос на получение подборки с id={}", compId);
        CompilationDto result = compilationService.getCompilationById(compId);
        log.info("Публичный: Найдена подборка: {}", result);
        return result;
    }
}