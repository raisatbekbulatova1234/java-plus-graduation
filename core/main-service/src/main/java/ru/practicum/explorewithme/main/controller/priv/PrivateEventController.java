package ru.practicum.explorewithme.main.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.main.dto.EventFullDto;
import ru.practicum.explorewithme.main.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.explorewithme.main.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.explorewithme.main.dto.EventShortDto;
import ru.practicum.explorewithme.main.dto.NewEventDto;
import ru.practicum.explorewithme.main.dto.UpdateEventUserRequestDto;
import ru.practicum.explorewithme.main.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.main.service.EventService;
import ru.practicum.explorewithme.main.service.RequestService;
import ru.practicum.explorewithme.main.service.params.EventRequestStatusUpdateRequestParams;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * ============================================================================
 * ПРИВАТНЫЙ КОНТРОЛЛЕР СОБЫТИЙ (для пользователей)
 * ============================================================================
 *
 * Обрабатывает запросы авторизованных пользователей для управления своими событиями.
 * Позволяет создавать, редактировать, просматривать события и управлять заявками.
 */
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;

    /**
     * Получение списка событий, созданных текущим пользователем.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsAddedByCurrentUser(
            @PathVariable Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {

        log.info("Пользователь id={}: Получен запрос на получение своих событий, from={}, size={}", userId, from, size);
        List<EventShortDto> events = eventService.getEventsByOwner(userId, from, size);
        log.info("Пользователь id={}: Найдено {} событий", userId, events.size());
        return events;
    }

    /**
     * Получение полной информации о своём событии по ID.
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getFullEventInfoByOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.info("Пользователь id={}: Получен запрос на получение полной информации о событии id={}", userId, eventId);
        EventFullDto eventFullDto = eventService.getEventPrivate(userId, eventId);
        log.info("Пользователь id={}: Событие id={} найдено", userId, eventFullDto.getId());
        return eventFullDto;
    }

    /**
     * Создание нового события.
     * Новое событие создаётся со статусом PENDING (ожидает модерации).
     */
    @PostMapping
    public ResponseEntity<EventFullDto> addEventPrivate(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Создание нового события {} пользователем с id {}", newEventDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEventPrivate(userId, newEventDto));
    }

    /**
     * Редактирование своего события.
     * Можно редактировать только события в статусе PENDING или CANCELED.
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {

        log.info("Пользователь id={}: Получен запрос на обновление события id={} с данными: {}",
                userId, eventId, updateEventUserRequestDto);

        EventFullDto updatedEvent = eventService.updateEventByOwner(userId, eventId, updateEventUserRequestDto);

        log.info("Пользователь id={}: Событие id={} успешно обновлено. Новый заголовок: {}",
                userId, eventId, updatedEvent.getTitle());
        return updatedEvent;
    }

    /**
     * Получение списка заявок на участие в событии (для инициатора события).
     */
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {
        log.info("Приватный: Получен запрос на получение заявок для события {} от инициатора {}", eventId, userId);
        List<ParticipationRequestDto> result = requestService.getEventRequests(userId, eventId);
        log.info("Приватный: Получен список заявок для события {}: {}", eventId, result);
        return result;
    }

    /**
     * Массовое изменение статусов заявок на участие (подтверждение/отклонение).
     * Доступно только инициатору события.
     */
    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResultDto updateRequestsStatus(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequestDto requestStatusUpdate) {
        log.info("Приватный: Получен запрос на изменение статуса заявок {} для события {} от инициатора {}",
                requestStatusUpdate.getRequestIds(), eventId, userId);
        EventRequestStatusUpdateRequestParams requestParams = EventRequestStatusUpdateRequestParams.builder()
                .userId(userId)
                .eventId(eventId)
                .requestIds(requestStatusUpdate.getRequestIds())
                .status(requestStatusUpdate.getStatus())
                .build();
        EventRequestStatusUpdateResultDto result = requestService.updateRequestsStatus(requestParams);
        log.info("Приватный: Результат изменения статусов заявок для события {}: {}", eventId, result);
        return result;
    }
}