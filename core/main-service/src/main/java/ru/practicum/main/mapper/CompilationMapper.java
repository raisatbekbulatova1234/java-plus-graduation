package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.dto.UpdateCompilationRequestDto;
import ru.practicum.main.model.Compilation;

/**
 * MapStruct маппер для сущности Compilation (подборка событий)
 */
@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {

    /**
     * Преобразует сущность в DTO для ответа клиенту
     * События (events) маппятся через EventMapper
     */
    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation compilation);

    /**
     * Преобразует DTO для создания в сущность
     * - id игнорируется (генерируется БД)
     * - events игнорируются (добавляются отдельно в сервисе)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    /**
     * Частичное обновление существующей подборки из DTO
     * - id игнорируется (нельзя изменить)
     * - events обновляются отдельно в сервисе
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    void updateCompilationFromDto(UpdateCompilationRequestDto dto, @MappingTarget Compilation compilation);
}