package ru.practicum.stats.server.mapper;

import ru.practicum.explorewithme.stats.dto.EndpointHitDto;
import ru.practicum.explorewithme.stats.server.model.EndpointHit;

/**
 * ============================================================================
 * МАППЕР ДЛЯ EndpointHit
 * ============================================================================
 *
 * Преобразует сущность EndpointHit (JPA) и DTO EndpointHitDto.
 */
public interface EndpointHitMapper {

    /**
     * Преобразует DTO в сущность для сохранения в БД.
     */
    EndpointHit toEndpointHit(EndpointHitDto dto);

    /**
     * Преобразует сущность в DTO для ответа клиенту.
     */
    EndpointHitDto toEndpointHitDto(EndpointHit entity);

}