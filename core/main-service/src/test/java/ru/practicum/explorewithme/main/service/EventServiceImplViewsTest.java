package ru.practicum.explorewithme.main.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explorewithme.main.dto.EventFullDto;
import ru.practicum.explorewithme.main.error.EntityNotFoundException;
import ru.practicum.explorewithme.main.mapper.EventMapper;
import ru.practicum.explorewithme.main.model.*;
import ru.practicum.explorewithme.main.repository.CategoryRepository;
import ru.practicum.explorewithme.main.repository.EventRepository;
import ru.practicum.explorewithme.main.repository.RequestRepository;
import ru.practicum.explorewithme.main.repository.UserRepository;
import ru.practicum.explorewithme.stats.client.StatsClient;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для просмотров событий (views)")
class EventServiceImplViewsTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private StatsClient statsClient;

    @InjectMocks
    private EventServiceImpl eventService;

    @Captor
    private ArgumentCaptor<EndpointHitDto> hitDtoCaptor;

    private Event testEvent;
    private EventFullDto testEventFullDto;
    private final Long eventId = 1L;
    private final String clientIp = "192.168.1.100";

    @BeforeEach
    void setUp() {
        Category category = Category.builder().id(10L).name("Test Category").build();
        User initiator = User.builder().id(100L).name("Test User").build();

        testEvent = Event.builder()
                .id(eventId)
                .title("Test Event")
                .annotation("Test Annotation")
                .description("Test Description")
                .category(category)
                .initiator(initiator)
                .state(EventState.PUBLISHED)
                .eventDate(LocalDateTime.now().plusDays(5))
                .createdOn(LocalDateTime.now().minusDays(1))
                .build();

        testEventFullDto = EventFullDto.builder()
                .id(eventId)
                .title("Test Event")
                .views(0L)
                .confirmedRequests(0L)
                .build();
    }

    @Test
    @DisplayName("Должен сохранять hit при просмотре события")
    void getEventByIdPublic_shouldSaveHit() {
        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.of(testEvent));
        when(requestRepository.countByEventIdAndStatus(eq(eventId), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);
        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(true)))
                .thenReturn(List.of(new ViewStatsDto("test-app", "/events/1", 1L)));
        when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

        eventService.getEventByIdPublic(eventId, clientIp);

        verify(statsClient).saveHit(hitDtoCaptor.capture());
        EndpointHitDto capturedHit = hitDtoCaptor.getValue();

        assertEquals("ewm-main-service", capturedHit.getApp());
        assertEquals("/events/" + eventId, capturedHit.getUri());
        assertEquals(clientIp, capturedHit.getIp());
        assertNotNull(capturedHit.getTimestamp());
    }

    @Test
    @DisplayName("Должен использовать переданный IP адрес при сохранении hit")
    void getEventByIdPublic_shouldUseProvidedIp() {
        String customIp = "10.0.0.5";

        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.of(testEvent));
        when(requestRepository.countByEventIdAndStatus(eq(eventId), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);
        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(true)))
                .thenReturn(List.of(new ViewStatsDto("test-app", "/events/1", 1L)));
        when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

        eventService.getEventByIdPublic(eventId, customIp);

        verify(statsClient).saveHit(hitDtoCaptor.capture());
        assertEquals(customIp, hitDtoCaptor.getValue().getIp());
    }

    @Test
    @DisplayName("Должен использовать 'unknown' IP если передан null")
    void getEventByIdPublic_shouldUseUnknownWhenIpIsNull() {
        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.of(testEvent));
        when(requestRepository.countByEventIdAndStatus(eq(eventId), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);
        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(true)))
                .thenReturn(List.of(new ViewStatsDto("test-app", "/events/1", 1L)));
        when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

        eventService.getEventByIdPublic(eventId, null);

        verify(statsClient).saveHit(hitDtoCaptor.capture());
        assertEquals("unknown", hitDtoCaptor.getValue().getIp());
    }

    @Test
    @DisplayName("Должен использовать 'unknown' IP если передан пустой IP")
    void getEventByIdPublic_shouldUseUnknownWhenIpIsBlank() {
        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.of(testEvent));
        when(requestRepository.countByEventIdAndStatus(eq(eventId), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);
        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(true)))
                .thenReturn(List.of(new ViewStatsDto("test-app", "/events/1", 1L)));
        when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

        eventService.getEventByIdPublic(eventId, "");

        verify(statsClient).saveHit(hitDtoCaptor.capture());
        assertEquals("unknown", hitDtoCaptor.getValue().getIp());
    }

    @Test
    @DisplayName("Должен корректно получать количество просмотров из statsClient")
    void getEventByIdPublic_shouldRetrieveViewsFromStatsClient() {
        long expectedViews = 42L;

        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.of(testEvent));
        when(requestRepository.countByEventIdAndStatus(eq(eventId), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);
        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(true)))
                .thenReturn(List.of(new ViewStatsDto("test-app", "/events/1", expectedViews)));
        when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

        EventFullDto result = eventService.getEventByIdPublic(eventId, clientIp);

        assertEquals(expectedViews, result.getViews());
    }

    @Test
    @DisplayName("Должен устанавливать views = 0 если statsClient вернул пустой список")
    void getEventByIdPublic_shouldSetViewsToZeroWhenNoStats() {
        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.of(testEvent));
        when(requestRepository.countByEventIdAndStatus(eq(eventId), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);
        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(true)))
                .thenReturn(List.of());
        when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

        EventFullDto result = eventService.getEventByIdPublic(eventId, clientIp);

        assertEquals(0L, result.getViews());
    }

    @Test
    @DisplayName("Должен устанавливать views = 0 если statsClient вернул null")
    void getEventByIdPublic_shouldSetViewsToZeroWhenStatsIsNull() {
        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.of(testEvent));
        when(requestRepository.countByEventIdAndStatus(eq(eventId), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);
        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(true)))
                .thenReturn(null);
        when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

        EventFullDto result = eventService.getEventByIdPublic(eventId, clientIp);

        assertEquals(0L, result.getViews());
    }

    @Test
    @DisplayName("Должен продолжать работу даже если statsClient.saveHit выбросил исключение")
    void getEventByIdPublic_shouldContinueWhenSaveHitFails() {
        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.of(testEvent));
        when(requestRepository.countByEventIdAndStatus(eq(eventId), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);
        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), eq(true)))
                .thenReturn(List.of(new ViewStatsDto("test-app", "/events/1", 5L)));
        when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

        doThrow(new RuntimeException("Stats service error")).when(statsClient).saveHit(any(EndpointHitDto.class));

        // Не должно выбросить исключение
        EventFullDto result = eventService.getEventByIdPublic(eventId, clientIp);

        assertNotNull(result);
        assertEquals(5L, result.getViews());
        verify(statsClient).saveHit(any(EndpointHitDto.class));
    }

    @Test
    @DisplayName("Должен выбрасывать EntityNotFoundException если событие не найдено или не опубликовано")
    void getEventByIdPublic_shouldThrowNotFoundWhenEventNotPublished() {
        when(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                eventService.getEventByIdPublic(eventId, clientIp));

        verify(statsClient, never()).saveHit(any(EndpointHitDto.class));
    }
}