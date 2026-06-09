package ru.practicum.explorewithme.main.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.explorewithme.main.dto.CategoryDto;
import ru.practicum.explorewithme.main.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * ============================================================================
 * ПУБЛИЧНЫЙ КОНТРОЛЛЕР КАТЕГОРИЙ
 * ============================================================================
 *
 * Обрабатывает запросы от неавторизованных пользователей для просмотра категорий.
 * Доступен без аутентификации.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCategoryController {

    private final CategoryService categoryService;

    /**
     * Получение списка всех категорий с пагинацией.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Публичный: Получен запрос на получение всех категорий, from={}, size={}", from, size);
        List<CategoryDto> result = categoryService.getAllCategories(from, size);
        log.info("Публичный: Получен список категорий: {} шт.", result.size());
        return result;
    }

    /**
     * Получение категории по ID.
     */
    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long categoryId) {
        log.info("Публичный: Получен запрос на получение категории с ID: {}", categoryId);
        CategoryDto result = categoryService.getCategoryById(categoryId);
        log.info("Публичный: Получена категория: {}", result);
        return result;
    }
}