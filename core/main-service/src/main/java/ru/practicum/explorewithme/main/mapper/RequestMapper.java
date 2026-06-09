package ru.practicum.explorewithme.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.main.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.main.model.ParticipationRequest;

/**
 * MapStruct маппер для сущности ParticipationRequest (запрос на участие)
 */
@Mapper(componentModel = "spring")
public interface RequestMapper {

    /**
     * Преобразует сущность в DTO для ответа клиенту
     * - requester.id → requesterId (ID пользователя, отправившего запрос)
     * - event.id → eventId (ID события, на которое запрашивается участие)
     */
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "event.id", target = "eventId")
    ParticipationRequestDto toRequestDto(ParticipationRequest participationRequest);
}