package ru.practicum.explorewithme.main.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.dto.CompilationDto;
import ru.practicum.explorewithme.main.dto.NewCompilationDto;
import ru.practicum.explorewithme.main.dto.UpdateCompilationRequestDto;
import ru.practicum.explorewithme.main.service.CompilationService;

/**
 * ============================================================================
 * АДМИНИСТРАТИВНЫЙ КОНТРОЛЛЕР ПОДБОРОК
 * ============================================================================
 *
 * Обрабатывает запросы от администраторов для управления подборками событий.
 * Позволяет создавать, обновлять и удалять подборки.
 */
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    /**
     * Создание новой подборки событий.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Админ: Получен запрос на создание подборки: {}", newCompilationDto);
        CompilationDto result = compilationService.saveCompilation(newCompilationDto);
        log.info("Админ: Создана подборка: {}", result);
        return result;
    }

    /**
     * Обновление существующей подборки.
     */
    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(
            @PathVariable @Positive Long compId,
            @Valid @RequestBody UpdateCompilationRequestDto updateCompilationRequestDto) {
        log.info("Админ: Получен запрос на обновление подборки id={} с данными: {}", compId, updateCompilationRequestDto);
        CompilationDto result = compilationService.updateCompilation(compId, updateCompilationRequestDto);
        log.info("Админ: Обновлена подборка: {}", result);
        return result;
    }

    /**
     * Удаление подборки по ID.
     */
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("Админ: Получен запрос на удаление подборки с id={}", compId);
        compilationService.deleteCompilation(compId);
        log.info("Админ: Удалена подборка с id={}", compId);
    }
}