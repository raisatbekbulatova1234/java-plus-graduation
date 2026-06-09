package ru.practicum.explorewithme.main.controller.admin;

import ru.practicum.explorewithme.main.dto.NewCategoryDto;
import ru.practicum.explorewithme.main.dto.CategoryDto;
import ru.practicum.explorewithme.main.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

/**
 * ============================================================================
 * АДМИНИСТРАТИВНЫЙ КОНТРОЛЛЕР КАТЕГОРИЙ
 * ============================================================================
 *
 * Обрабатывает запросы от администраторов для управления категориями событий.
 * Все методы доступны только пользователям с ролью ADMIN.
 *
 * Базовый путь: /admin/categories
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * Создаёт новую категорию событий.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // HTTP 201 Created
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Админ: Получен запрос на добавление категории: {}", newCategoryDto);
        CategoryDto result = categoryService.createCategory(newCategoryDto);
        log.info("Админ: Категория успешно добавлена: {}", result);
        return result;
    }

    /**
     * Обновляет существующую категорию по ID.
     */
    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)      // HTTP 200 OK
    public CategoryDto updateCategory(@PathVariable Long categoryId,
                                      @Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("Админ: Получен запрос на обновление категории с ID: {}, новые данные: {}", categoryId, categoryDto);
        CategoryDto result = categoryService.updateCategory(categoryId, categoryDto);
        log.info("Админ: Категория успешно обновлена: {}", result);
        return result;
    }

    /**
     * Удаляет категорию по ID.
     *
     * Важно: Категория может быть удалена ТОЛЬКО если у неё нет связанных событий.
     * Если категория содержит события - будет выброшено исключение.
     */
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // HTTP 204 No Content (тело ответа пустое)
    public void deleteCategory(@PathVariable Long categoryId) {
        log.info("Админ: Получен запрос на удаление категории с ID: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        log.info("Админ: Категория с ID {} успешно удалена", categoryId);
    }
}