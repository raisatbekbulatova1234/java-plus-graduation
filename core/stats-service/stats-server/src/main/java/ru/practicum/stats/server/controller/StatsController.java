package ru.practicum.stats.server.controller;

import static ru.practicum.explorewithme.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.stats.client.StatsClient;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;
import ru.practicum.explorewithme.stats.server.service.StatsService;
import ru.practicum.stats.server.service.StatsService;

/**
 * ============================================================================
 * КОНТРОЛЛЕР СЕРВИСА СТАТИСТИКИ
 * ============================================================================
 *
 * Обрабатывает запросы на сохранение и получение статистики обращений к API.
 * Реализует интерфейс StatsClient для унификации клиентской и серверной части.
 *
 * Базовый путь: корневой (/)
 * Эндпоинты:
 *   POST /hit  - сохранение информации о запросе
 *   GET  /stats - получение статистики за период
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class StatsController implements StatsClient {

    private final ru.practicum.stats.server.service.StatsService statsService;

    /**
     * Сохранение информации о запросе к эндпоинту.
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)  // HTTP 201 Created
    public void saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Контроллер: получен запрос на сохранение хита");
        log.debug("Сохранение хита: {}", endpointHitDto);
        statsService.saveHit(endpointHitDto);
    }

    /**
     * Получение статистики по посещениям за указанный период.
     */
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)  // HTTP 200 OK
    public List<ViewStatsDto> getStats(
            @RequestParam(name = "start")
            @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
            LocalDateTime start,

            @RequestParam(name = "end")
            @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
            LocalDateTime end,

            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {

        log.info("Контроллер: получен запрос на получение статистики");
        log.debug("Параметры запроса: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        return statsService.getStats(start, end, uris, unique);
    }
}