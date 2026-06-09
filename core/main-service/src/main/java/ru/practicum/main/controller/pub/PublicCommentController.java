package ru.practicum.main.controller.pub;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.service.CommentService;
import ru.practicum.main.service.params.PublicCommentParameters;

import java.util.List;

/**
 * ============================================================================
 * ПУБЛИЧНЫЙ КОНТРОЛЛЕР КОММЕНТАРИЕВ
 * ============================================================================
 *
 * Обрабатывает запросы от неавторизованных пользователей для просмотра комментариев.
 * Доступен без аутентификации.
 */
@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCommentController {

    private final CommentService commentService;

    /**
     * Получение списка комментариев к событию с пагинацией и сортировкой.
     * Параметры:
     * - from  - количество пропускаемых элементов (по умолчанию 0)
     * - size  - размер страницы (по умолчанию 10)
     * - sort  - сортировка по дате создания: createdOn,ASC (по возрастанию)
     *           или createdOn,DESC (по убыванию, по умолчанию)
     *
     * Примечание: Удалённые комментарии (isDeleted = true) не отображаются.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsForEventId(
            @PathVariable @Positive Long eventId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size,
            @Pattern(regexp = "^(createdOn),(ASC|DESC)$",
                    message = "Параметр sort должен иметь формат createdOn,ASC|DESC")
            @RequestParam(defaultValue = "createdOn,DESC") String sort) {
        log.info("Публичный: Получен запрос на получение комментариев для события id={}, параметры: from={}, size={}, sort={}",
                eventId, from, size, sort);

        // Определение направления сортировки
        Sort sortingRule;
        if (sort != null && sort.equalsIgnoreCase("createdOn,ASC")) {
            sortingRule = Sort.by(Sort.Direction.ASC, "createdOn");
        } else {
            sortingRule = Sort.by(Sort.Direction.DESC, "createdOn");
        }

        PublicCommentParameters parameters = PublicCommentParameters.builder()
                .from(from)
                .size(size)
                .sort(sortingRule)
                .build();

        List<CommentDto> result = commentService.getCommentsForEvent(eventId, parameters);
        log.info("Публичный: Получен список комментариев для события id={}, найдено: {} шт.", eventId, result.size());
        return result;
    }
}