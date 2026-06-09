package ru.practicum.stats.client;

import static ru.practicum.common.constants.DateTimeConstants.DATE_TIME_FORMAT_PATTERN;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * FEIGN КЛИЕНТ ДЛЯ СЕРВИСА СТАТИСТИКИ
 * ============================================================================
 *
 * Декларативный HTTP клиент для взаимодействия с stats-server.
 *
 * Использование:
 * 1. Добавить зависимость stats-client в pom.xml сервиса
 * 2. Включить Feign через @EnableFeignClients
 * 3. Внедрить StatsClient и вызывать методы
 *
 */
@FeignClient(name = "stats-server")  // Имя сервиса в Eureka
public interface StatsClient {

    /**
     * Отправка информации об обращении к эндпоинту.
     */
    @PostMapping("/hit")
    void saveHit(@RequestBody EndpointHitDto endpointHitDto);

    /**
     * Получение статистики обращений за период.
     */
    @GetMapping("/stats")
    List<ViewStatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN) LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") Boolean unique);
}