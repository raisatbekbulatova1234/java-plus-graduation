package ewm.event.service;

import client.StatsClient;
import ewm.category.model.Category;
import ewm.category.repository.CategoryRepository;
import ewm.common.exception.BadRequestException;
import ewm.common.exception.ConflictException;
import ewm.common.exception.NotFoundException;
import ewm.event.dto.*;
import ewm.common.dto.event.*;
import ewm.event.client.RequestClient;
import ewm.event.client.dto.EventInternalDto;
import ewm.event.mapper.EventMapper;
import ewm.event.model.Event;
import ewm.event.model.EventSort;
import ewm.event.model.EventState;
import ewm.event.model.EventStateActionAdmin;
import ewm.event.repository.DatabaseEventSearchRepository;
import ewm.event.repository.EventRepository;
import ewm.user.client.UserClient;
import ewm.common.dto.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserClient userClient;
    private final EventRepository eventRepository;
    private final DatabaseEventSearchRepository databaseEventSearchRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final RequestClient requestClient;

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        log.info("Creating new event for user id: {}", userId);

        isEventTimeValid(eventDto.getEventDate());

        UserDto user = userClient.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new NotFoundException("User not found");
                });

        Event event = EventMapper.mapToEvent(user, eventDto, eventDto.getCategory());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event = eventRepository.save(event);

        log.info("Event created successfully with id: {} for user: {}", event.getId(), userId);

        List<Event> eventList = List.of(event);

        return this.mapToEventFullDto(eventList).getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto get(Long userId, Long eventId) {
        log.debug("Getting event id: {} for user id: {}", eventId, userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id: {}", eventId);
                    return new NotFoundException("Event not found");
                });

        if (!event.getInitiatorId().equals(userId)) {
            log.error("User {} not authorized to access event {}", userId, eventId);
            throw new NotFoundException("Event not found");
        }

        List<Event> eventList = List.of(event);

        return this.mapToEventFullDto(eventList).getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> get(List<Long> users,
                                  List<EventState> states,
                                  List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  int from,
                                  int size) {
        log.debug("Getting events with filters - users: {}, states: {}, categories: {}, from: {}, size: {}",
                users, states, categories, from, size);

        Pageable page = PageRequest.of(from / size, size);

        List<Event> eventList = databaseEventSearchRepository.findForAdmin(users, states, categories, rangeStart, rangeEnd, page);

        log.debug("Found {} events matching filters", eventList.size());
        return this.mapToEventFullDto(eventList);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEvent(Long eventId, HttpServletRequest request) {
        log.info("Getting public event id: {} from IP: {}", eventId, request.getRemoteAddr());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id: {}", eventId);
                    return new NotFoundException("Event not found");
                });

        if (event.getState() != EventState.PUBLISHED) {
            log.warn("Attempt to access unpublished event id: {} with state: {}", eventId, event.getState());
            throw new NotFoundException("Event is not published");
        }

        List<Event> eventList = List.of(event, event);
        registerHit(request);

        log.info("Successfully retrieved public event id: {}", eventId);
        return this.mapToEventFullDto(eventList).getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        log.debug("Getting events for user id: {}, from: {}, size: {}", userId, from, size);

        userClient.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new NotFoundException("User not found");
                });

        Pageable page = PageRequest.of(from / size, size);

        List<Event> eventList = eventRepository.findByInitiatorId(userId, page);

        log.debug("Found {} events for user id: {}", eventList.size(), userId);

        return this.mapToEventShortDto(eventList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               EventSort sort,
                                               int from,
                                               int size,
                                               HttpServletRequest request) {

        log.info("Searching public events with filters - text: {}, categories: {}, paid: {}, sort: {}, from: {}, size: {}",
                text, categories, paid, sort, from, size);

        if (rangeStart == null) rangeStart = LocalDateTime.now();
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            log.warn("Invalid date range: start={}, end={}", rangeStart, rangeEnd);
            throw new BadRequestException("Range start must be before rangeEnd");
        }

        // Pageable: сортировка только по eventDate, не по views
        Pageable page;
        if (sort == EventSort.EVENT_DATE) {
            page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        } else {
            page = PageRequest.of(from / size, size);
        }

        registerHit(request);

        List<Event> eventList = databaseEventSearchRepository.findPublicEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page
        );

        List<EventShortDto> dtos = mapToEventShortDto(eventList);

        log.debug("Found {} events matching public search criteria", eventList.size());

        if (sort == EventSort.VIEWS) {
            dtos.sort(Comparator.comparingLong(EventShortDto::getViews).reversed());
            log.debug("Sorted events by views");
        }

        return dtos;
    }


    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Updating event id: {} for user id: {}", eventId, userId);

        Event currentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id: {}", eventId);
                    return new NotFoundException("Event not found");
                });

        if (currentEvent.getState().equals(EventState.PUBLISHED)) {
            log.warn("Attempt to update already published event id: {}", eventId);
            throw new ConflictException("Event is already published");
        }

        if (!currentEvent.getInitiatorId().equals(userId)) {
            log.warn("User {} not authorized to update event {}", userId, eventId);
            throw new BadRequestException("User not allowed to update event");
        }

        Event updatedEvent = EventMapper.updateEvent(currentEvent, updateEventUserRequest);

        if (updateEventUserRequest.hasCategory() &&
                !updatedEvent.getCategoryId().equals(updateEventUserRequest.getCategory())) {
            updatedEvent.setCategoryId(updateEventUserRequest.getCategory());
            log.debug("Updated category for event {} to {}", eventId, updateEventUserRequest.getCategory());
        }

        isEventTimeValid(updatedEvent.getEventDate());
        updatedEvent = eventRepository.save(updatedEvent);

        log.info("Event id: {} successfully updated by user id: {}", eventId, userId);

        List<Event> eventList = List.of(updatedEvent);

        return this.mapToEventFullDto(eventList).getFirst();
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Admin updating event id: {}", eventId);

        Event currentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id: {}", eventId);
                    return new NotFoundException("Event not found");
                });

        if (currentEvent.getState().equals(EventState.PUBLISHED) &&
                updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isAfter(currentEvent.getPublishedOn().minusHours(1))) {
            log.warn("Invalid event time for event id: {}, eventDate: {}, publishedOn: {}",
                    eventId, updateEventAdminRequest.getEventDate(), currentEvent.getPublishedOn());
            throw new ConflictException("Invalid event time");
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (currentEvent.getState().equals(EventState.PENDING)) {
                if (updateEventAdminRequest.getStateAction().equals(EventStateActionAdmin.PUBLISH_EVENT)) {
                    currentEvent.setState(EventState.PUBLISHED);
                    currentEvent.setPublishedOn(LocalDateTime.now());
                    log.info("Event id: {} published by admin", eventId);
                } else {
                    currentEvent.setState(EventState.CANCELED);
                    log.info("Event id: {} canceled by admin", eventId);
                }
            } else {
                log.warn("Invalid event state for event id: {}, current state: {}", eventId, currentEvent.getState());
                throw new ConflictException("Invalid event state");
            }
        }

        Event updatedEvent = EventMapper.updateEvent(currentEvent, updateEventAdminRequest);

        if (updateEventAdminRequest.hasCategory() &&
                !updatedEvent.getCategoryId().equals(updateEventAdminRequest.getCategory())) {
            updatedEvent.setCategoryId(updateEventAdminRequest.getCategory());
            log.debug("Updated category for event {} to {}", eventId, updateEventAdminRequest.getCategory());
        }

        updatedEvent = eventRepository.save(updatedEvent);

        log.info("Event id: {} successfully updated by admin", eventId);

        List<Event> eventList = List.of(updatedEvent);

        return this.mapToEventFullDto(eventList).getFirst();
    }

    private void isEventTimeValid(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Invalid event time: {}, must be at least 2 hours from now", eventTime);
            throw new BadRequestException("Invalid event time");
        }
    }

    private void registerHit(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("main-service");
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());

        try {
            statsClient.hit(endpointHitDto);
            log.debug("Hit registered for URI: {}, IP: {}", request.getRequestURI(), request.getRemoteAddr());
        } catch (Exception e) {
            log.error("Failed to register hit for URI: {}, IP: {}, Error: {}",
                    request.getRequestURI(), request.getRemoteAddr(), e.getMessage(), e);
        }
    }

    private Map<Long, Integer> getEventsViews(List<Event> eventList) {
        if (eventList == null || eventList.isEmpty()) {
            log.debug("Empty event list, returning empty views map");
            return Map.of();
        }

        List<String> uris = eventList.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        LocalDateTime start = eventList.stream()
                .map(Event::getCreatedOn)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now().minusYears(1));

        LocalDateTime end = LocalDateTime.now();

        log.debug("Fetching views for {} events, uris: {}, start: {}, end: {}",
                eventList.size(), uris, start, end);

        try {
            List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);
            log.debug("Received {} view stats records", stats.size());

            Map<Long, Integer> map = new HashMap<>();
            for (ViewStatsDto s : stats) {
                String[] parts = s.getUri().split("/");
                if (parts.length >= 3) {
                    long eventId = Long.parseLong(parts[2]);
                    map.put(eventId, (int) s.getHits());
                    log.trace("Event id: {} has {} views", eventId, s.getHits());
                }
            }
            return map;
        } catch (Exception ex) {
            log.error("Failed to fetch views for events. URIs: {}, Start: {}, End: {}. Error: {}",
                    uris, start, end, ex.getMessage(), ex);
            return Map.of();
        }
    }

    private List<EventFullDto> mapToEventFullDto(List<Event> eventList) {
        log.debug("Mapping {} events to EventFullDto", eventList.size());

        Map<Long, Integer> views = getEventsViews(eventList);
        Map<Long, Long> confirmed = getConfirmedRequests(eventList);

        return eventList.stream()
                .map(e -> EventMapper.mapToEventFullDto(
                        e,
                        views.getOrDefault(e.getId(), 0),
                        confirmed.getOrDefault(e.getId(), 0L)
                ))
                .toList();
    }


    private Map<Long, Long> getConfirmedRequests(List<Event> eventList) {
        if (eventList == null || eventList.isEmpty()) {
            log.debug("Empty event list, returning empty confirmed requests map");
            return Map.of();
        }

        List<Long> ids = eventList.stream()
                .map(Event::getId)
                .toList();

        log.debug("Fetching confirmed requests for event ids: {}", ids);

        try {
            List<RequestClient.EventConfirmedCountDto> results = requestClient.countConfirmedByEventIds(ids);
            log.debug("Received {} confirmed request records", results.size());

            Map<Long, Long> map = new HashMap<>();
            for (RequestClient.EventConfirmedCountDto row : results) {
                map.put(row.getEventId(), row.getCnt());
                log.trace("Event id: {} has {} confirmed requests", row.getEventId(), row.getCnt());
            }
            return map;
        } catch (Exception e) {
            log.error("Failed to fetch confirmed requests for events IDs: {}. Error: {}", ids, e.getMessage(), e);
            return Map.of();
        }
    }


    private List<EventShortDto> mapToEventShortDto(List<Event> eventList) {
        log.debug("Mapping {} events to EventShortDto", eventList.size());

        Map<Long, Integer> views = getEventsViews(eventList);
        Map<Long, Long> confirmed = getConfirmedRequests(eventList);

        return eventList.stream()
                .map(e -> EventMapper.mapToEventShortDto(
                        e,
                        views.getOrDefault(e.getId(), 0),
                        confirmed.getOrDefault(e.getId(), 0L)
                ))
                .toList();
    }
}