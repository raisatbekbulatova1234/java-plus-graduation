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

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsAddedByCurrentUser(
            @PathVariable @Positive Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {

        log.info("User id={}: Received request to get own events, from={}, size={}", userId, from, size);
        List<EventShortDto> events = eventService.getEventsByOwner(userId, from, size);
        log.info("User id={}: Found {} events. From={}, size={}", userId, events.size(), from, size);
        return events;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getFullEventInfoByOwner(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {

        log.info("User id={}: Received request to get full info for event id={}", userId, eventId);
        EventFullDto eventFullDto = eventService.getEventPrivate(userId, eventId);
        log.info("User id={}: Found full info for event id={}: {}", userId, eventId, eventFullDto.getId());
        return eventFullDto;
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addEventPrivate(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Создание нового события {} зарегистрированным пользователем c id {}", newEventDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEventPrivate(userId, newEventDto));
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByOwner(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {

        log.info("User id={}: Received request to update event id={} with data: {}",
                userId, eventId, updateEventUserRequestDto);

        EventFullDto updatedEvent = eventService.updateEventByOwner(userId, eventId, updateEventUserRequestDto);

        log.info("User id={}: Event id={} updated successfully. New title: {}",
                userId, eventId, updatedEvent.getTitle());
        return updatedEvent;
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {
        log.info("Private: Received request to get list requests for event {} when initiator {}", eventId, userId);
        List<ParticipationRequestDto> result = requestService.getEventRequests(userId, eventId);
        log.info("Private: Received list requests for event {} when initiator {} : {}", eventId, userId, result);
        return result;
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResultDto updateRequestsStatus(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequestDto requestStatusUpdate) {
        log.info("Private: Received request to change status requests {} for event {} when initiator {}",
                requestStatusUpdate.getRequestIds(), eventId, userId);
        EventRequestStatusUpdateRequestParams requestParams = EventRequestStatusUpdateRequestParams.builder()
                .userId(userId)
                .eventId(eventId)
                .requestIds(requestStatusUpdate.getRequestIds())
                .status(requestStatusUpdate.getStatus())
                .build();
        EventRequestStatusUpdateResultDto result = requestService.updateRequestsStatus(requestParams);
        log.info("Private: Received list requests for event {} when initiator {} : {}", eventId, userId, result);
        return result;
    }
}