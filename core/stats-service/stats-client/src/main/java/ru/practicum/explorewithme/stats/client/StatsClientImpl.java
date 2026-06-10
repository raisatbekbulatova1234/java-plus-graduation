package ru.practicum.explorewithme.stats.client;

import static ru.practicum.explorewithme.common.constants.DateTimeConstants.DATE_TIME_FORMATTER;

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
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;

@Service
@Slf4j
public class StatsClientImpl implements StatsClient {

    private final RestClient restClient;

    @Autowired
    public StatsClientImpl(@Value("${stats-server.url}") String statsServerUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsServerUrl)
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

    public StatsClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void saveHit(EndpointHitDto endpointHitDto) {
        log.info("Отправка данных статистики: app={}, uri={}, ip={}, timestamp={}",
                endpointHitDto.getApp(), endpointHitDto.getUri(), endpointHitDto.getIp(), endpointHitDto.getTimestamp());

        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto)
                .retrieve()
                .toBodilessEntity();

        log.debug("Статистика успешно сохранена");
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        // Исправлено: если unique == null, используем false
        boolean uniqueParam = (unique != null) ? unique : true;

        log.info("Запрос статистики: start={}, end={}, uris={}, unique={}", start, end, uris, uniqueParam);

        List<ViewStatsDto> stats = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", start.format(DATE_TIME_FORMATTER))
                            .queryParam("end", end.format(DATE_TIME_FORMATTER));

                    if (uris != null && !uris.isEmpty()) {
                        for (String uri : uris) {
                            uriBuilder.queryParam("uris", uri);
                        }
                    }

                    // Исправлено: всегда передаем параметр unique
                    uriBuilder.queryParam("unique", uniqueParam);

                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        log.info("Получена статистика: {} записей", stats != null ? stats.size() : 0);
        if (stats != null && !stats.isEmpty()) {
            for (ViewStatsDto stat : stats) {
                log.debug("Статистика: uri={}, hits={}", stat.getUri(), stat.getHits());
            }
        }

        return stats;
    }
}