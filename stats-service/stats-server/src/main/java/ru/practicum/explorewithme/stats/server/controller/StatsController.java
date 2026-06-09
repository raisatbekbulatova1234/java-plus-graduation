package ru.practicum.explorewithme.stats.server.controller;

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
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;
import ru.practicum.explorewithme.stats.server.service.StatsService;

/**
 * REST-контроллер для работы со статистикой
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class StatsController {

    private final StatsService statsService;

    /**
     * Сохраняет информацию о том, что к эндпоинту был выполнен запрос
     *
     * @param endpointHitDto данные о запросе
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Контроллер: получен запрос на сохранение нового обращения");
        log.debug("Сохранение нового обращения: {}", endpointHitDto);
        statsService.saveHit(endpointHitDto);
    }

    /**
     * Возвращает статистику по посещениям
     *
     * @param start  дата и время начала диапазона (формат "yyyy-MM-dd HH:mm:ss")
     * @param end    дата и время конца диапазона (формат "yyyy-MM-dd HH:mm:ss")
     * @param uris   список URI, для которых нужно получить статистику (необязательный)
     * @param unique учитывать только уникальные посещения (необязательный, по умолчанию false)
     * @return список DTO со статистикой
     */
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
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