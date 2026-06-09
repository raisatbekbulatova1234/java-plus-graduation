package ru.practicum.main.service;

import java.util.List;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.UpdateEventAdminRequestDto;
import ru.practicum.main.dto.UpdateEventUserRequestDto;
import ru.practicum.main.service.params.AdminEventSearchParams;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.service.params.PublicEventSearchParams;

public interface EventService {
    List<EventFullDto> getEventsAdmin(
        AdminEventSearchParams params,
        int from,
        int size
    );

    List<EventShortDto> getEventsByOwner(Long userId, int from, int size);

    EventFullDto getEventPrivate(Long userId, Long eventId);

    EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequestDto requestDto);

    EventFullDto moderateEventByAdmin(Long eventId, UpdateEventAdminRequestDto requestDto);

    List<EventShortDto> getEventsPublic(PublicEventSearchParams params, int from, int size);

    EventFullDto getEventByIdPublic(Long eventId);
}