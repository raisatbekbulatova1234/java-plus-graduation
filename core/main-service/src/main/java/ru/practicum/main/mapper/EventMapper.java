package ru.practicum.main.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventState;

/**
 * MapStruct маппер для сущности Event (событие)
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    /**
     * Преобразует сущность в полный DTO (EventFullDto)
     * - confirmedRequestsCount → confirmedRequests (количество подтверждённых заявок)
     * - views игнорируется (заполняется отдельно через сервис статистики)
     */
    @Mappings({
            @Mapping(source = "confirmedRequestsCount", target = "confirmedRequests"),
            @Mapping(target = "views", ignore = true)
    })
    EventFullDto toEventFullDto(Event event);

    /**
     * Преобразует DTO для создания события в сущность
     * - id игнорируется (генерируется БД)
     * - publishedOn игнорируется (устанавливается при публикации)
     * - compilations игнорируются (добавляются позже)
     * - initiator игнорируется (устанавливается в сервисе)
     * - createdOn игнорируется (заполняется автоматически)
     * - confirmedRequestsCount игнорируется (вычисляется через @Formula)
     * - state принудительно устанавливается в PENDING (ожидает модерации)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "confirmedRequestsCount", ignore = true)
    @Mapping(target = "state", expression = "java(ru.practicum.main.model.EventState.PENDING)")
    Event toEvent(NewEventDto newEventDto);

    /**
     * Преобразует список сущностей в список полных DTO
     */
    List<EventFullDto> toEventFullDtoList(List<Event> events);

    /**
     * Преобразует сущность в сокращённый DTO (EventShortDto) для списков
     */
    @Mappings({
            @Mapping(source = "confirmedRequestsCount", target = "confirmedRequests"),
            @Mapping(target = "views", ignore = true)
    })
    EventShortDto toEventShortDto(Event event);

    /**
     * Преобразует список сущностей в список сокращённых DTO
     */
    List<EventShortDto> toEventShortDtoList(List<Event> events);
}