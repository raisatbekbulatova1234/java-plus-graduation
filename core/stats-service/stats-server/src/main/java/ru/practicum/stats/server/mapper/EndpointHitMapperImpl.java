package ru.practicum.stats.server.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;
/**
 * ============================================================================
 * РЕАЛИЗАЦИЯ МАППЕРА ДЛЯ EndpointHit
 * ============================================================================
 *
 * Реализует преобразование между сущностью EndpointHit и DTO EndpointHitDto.
 * Вручную (без MapStruct) для простоты и минимальных зависимостей.
 */
@Component
public class EndpointHitMapperImpl implements EndpointHitMapper {

    @Override
    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        if (dto == null) {
            return null;
        }

        return EndpointHit.builder().app(dto.getApp()).uri(dto.getUri()).ip(dto.getIp())
                .timestamp(dto.getTimestamp()).build();
    }

    @Override
    public EndpointHitDto toEndpointHitDto(EndpointHit entity) {
        if (entity == null) {
            return null;
        }

        return EndpointHitDto.builder().app(entity.getApp()).uri(entity.getUri()).ip(entity.getIp())
                .timestamp(entity.getTimestamp()).build();

    }
}