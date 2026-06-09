package ru.practicum.explorewithme.main.controller.admin;

import static ru.practicum.explorewithme.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.dto.EventFullDto;
import ru.practicum.explorewithme.main.dto.UpdateEventAdminRequestDto;
import ru.practicum.explorewithme.main.model.EventState;
import ru.practicum.explorewithme.main.service.EventService;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.explorewithme.main.service.params.AdminEventSearchParams;

/**
 * ============================================================================
 * АДМИНИСТРАТИВНЫЙ КОНТРОЛЛЕР СОБЫТИЙ
 * ============================================================================
 *
 * Обрабатывает запросы от администраторов для управления событиями.
 * Позволяет искать события с фильтрацией, публиковать и отклонять события.
 */
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventController {

    private final EventService eventService;
    private static final String DATETIME_FORMAT = DATE_TIME_FORMAT_PATTERN;

    /**
     * Поиск событий администратором с фильтрацией.
     * Возвращает полную информацию о событиях, подходящих под условия.
     * Если ничего не найдено - возвращает пустой список.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEventsAdmin(
            @RequestParam(name = "users", required = false) List<Long> users,
            @RequestParam(name = "states", required = false) List<EventState> states,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = DATETIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = DATETIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {

        log.info("Админ: Получен запрос на поиск событий с параметрами: users={}, states={}, categories={}, " +
                        "rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        AdminEventSearchParams params = AdminEventSearchParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        List<EventFullDto> foundEvents = eventService.getEventsAdmin(params, from, size);
        log.info("Админ: Найдено {} событий по заданным критериям", foundEvents.size());
        return foundEvents;
    }

    /**
     * Модерация события администратором.
     * Позволяет опубликовать или отклонить событие.
     * Правила:
     * - Дата события должна быть не ранее чем за час от даты публикации
     * - Опубликовать можно только событие в статусе PENDING
     * - Отклонить можно только неопубликованное событие
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto moderateEventByAdmin(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminRequestDto) {

        log.info("Админ: Получен запрос на модерацию события id={} с данными: {}",
                eventId, updateEventAdminRequestDto);

        EventFullDto moderatedEvent = eventService.moderateEventByAdmin(eventId, updateEventAdminRequestDto);

        log.info("Админ: Событие id={} успешно промодерировано. Новый статус: {}",
                eventId, moderatedEvent.getState());
        return moderatedEvent;
    }
}