package ru.practicum.stats.server.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.dto.ViewStatsDto;
import ru.practicum.explorewithme.stats.server.mapper.EndpointHitMapper;
import ru.practicum.explorewithme.stats.server.model.EndpointHit;
import ru.practicum.explorewithme.stats.server.repository.StatsRepository;

/**
 * ============================================================================
 * РЕАЛИЗАЦИЯ СЕРВИСА СТАТИСТИКИ
 * ============================================================================
 *
 * Обеспечивает сохранение обращений и получение статистики.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class StatsServiceImpl implements StatsService {

    private final EndpointHitMapper endpointHitMapper;
    private final StatsRepository statsRepository;

    /**
     * Сохраняет информацию об обращении к эндпоинту.
     */
    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        log.debug("Сервис: попытка сохранить обращение: {}", endpointHitDto);

        if (endpointHitDto == null) {
            log.warn("Сервис: невозможно сохранить обращение, входной DTO = null");
            throw new IllegalArgumentException("EndpointHitDto не может быть null");
        }

        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(endpointHitDto);
        statsRepository.save(endpointHit);
        log.info("Сервис: обращение успешно сохранено для app: {}, uri: {}",
                endpointHit.getApp(), endpointHit.getUri());
    }

    /**
     * Возвращает статистику обращений за указанный период.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, boolean unique) {
        log.debug("Сервис: запрос статистики с параметрами: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        // Проверка, что дата начала не позже даты конца
        if (start != null && end != null && start.isAfter(end)) {
            log.warn("Ошибка валидации: дата начала {} позже даты конца {}", start, end);
            throw new IllegalArgumentException("Дата начала не может быть позже даты конца");
        }

        // Пустой список URI заменяем на null (для корректной работы запроса)
        Collection<String> urisForRepo = (uris == null || uris.isEmpty()) ? null : uris;

        List<ViewStatsDto> stats;
        if (unique) {
            stats = statsRepository.findUniqueStats(start, end, urisForRepo);
        } else {
            stats = statsRepository.findStats(start, end, urisForRepo);
        }

        log.info("Сервис: найдено {} записей статистики", stats.size());
        return stats;
    }
}