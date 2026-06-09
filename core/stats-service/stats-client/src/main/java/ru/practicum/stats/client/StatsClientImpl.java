package ru.practicum.stats.client;

import static ru.practicum.common.constants.DateTimeConstants.DATE_TIME_FORMATTER;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

/**
 * ============================================================================
 * РЕАЛИЗАЦИЯ КЛИЕНТА СТАТИСТИКИ (REST CLIENT)
 * ============================================================================
 *
 * Использует Spring RestClient для отправки статистики в stats-server.
 * Альтернатива Feign-клиенту (не требует Feign аннотаций).
 *
 * Особенности:
 * - Автоматическая обработка ошибок (4xx, 5xx)
 * - Формирование URI с параметрами
 * - Асинхронный или синхронный режим (по умолчанию синхронный)
 */
@Service
@Slf4j
public class StatsClientImpl implements StatsClient {

    private final RestClient restClient;

    /**
     * Конструктор для создания клиента с базовым URL из конфигурации.
     *
     * @param statsServerUrl URL сервера статистики (из application.yml)
     */
    @Autowired
    public StatsClientImpl(@Value("${stats-server.url}") String statsServerUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsServerUrl)
                // Обработчик ошибок HTTP статусов (4xx, 5xx)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String errorMessage = "Ошибка при обращении к сервису статистики: " +
                            response.getStatusCode() + " " + response.getStatusText();
                    log.error(errorMessage);

                    if (response.getStatusCode().is4xxClientError()) {
                        throw new RestClientException("Ошибка клиентского запроса: " + errorMessage);
                    } else if (response.getStatusCode().is5xxServerError()) {
                        throw new RestClientException("Ошибка сервера статистики: " + errorMessage);
                    } else {
                        throw new RestClientException(errorMessage);
                    }
                })
                .build();
    }

    /**
     * Конструктор для тестов (позволяет подменить RestClient моком).
     */
    public StatsClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Отправка данных об обращении в сервис статистики.
     *
     * POST /hit
     */
    @Override
    public void saveHit(EndpointHitDto endpointHitDto) {
        log.debug("Отправка данных статистики: {}", endpointHitDto);
        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto)
                .retrieve()
                .toBodilessEntity();  // Тело ответа не ожидается (только статус)
        log.debug("Статистика успешно сохранена");
    }

    /**
     * Получение статистики из сервиса статистики.
     *
     * GET /stats?start=...&end=...&uris=...&unique=...
     */
    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, Boolean unique) {
        log.debug("Запрос статистики: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        List<ViewStatsDto> stats = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", start.format(DATE_TIME_FORMATTER))
                            .queryParam("end", end.format(DATE_TIME_FORMATTER));

                    // Добавление параметра uris (может быть несколько)
                    if (uris != null && !uris.isEmpty()) {
                        for (String uri : uris) {
                            uriBuilder.queryParam("uris", uri);
                        }
                    }

                    if (unique != null) {
                        uriBuilder.queryParam("unique", unique);
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<List<ViewStatsDto>>() {});

        log.debug("Получена статистика: {}", stats);
        return stats;
    }
}