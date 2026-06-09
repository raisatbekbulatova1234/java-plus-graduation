package ru.practicum.explorewithme.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.explorewithme.main.dto.CommentAdminDto;
import ru.practicum.explorewithme.main.dto.CommentDto;
import ru.practicum.explorewithme.main.dto.NewCommentDto;
import ru.practicum.explorewithme.main.model.Comment;

import java.util.List;

/**
 * MapStruct маппер для сущности Comment
 * Преобразует Comment <-> CommentDto <-> CommentAdminDto <-> NewCommentDto
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    // =========================================================================
    // ПРЕОБРАЗОВАНИЕ NewCommentDto → Comment (создание нового комментария)
    // =========================================================================

    /**
     * Преобразует DTO для создания комментария в сущность
     * - id игнорируется (генерируется БД)
     * - createdOn/updatedOn игнорируются (заполняются автоматически через JPA Auditing)
     * - author/event игнорируются (устанавливаются в сервисе)
     * - isEdited/isDeleted игнорируются (значения по умолчанию false)
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdOn", ignore = true),
            @Mapping(target = "updatedOn", ignore = true),
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "event", ignore = true),
            @Mapping(target = "isEdited", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    Comment toComment(NewCommentDto newCommentDto);

    // =========================================================================
    // ПРЕОБРАЗОВАНИЕ Comment → CommentDto (для публичного API)
    // =========================================================================

    /**
     * Преобразует сущность в DTO для пользователей
     * - eventId извлекается из связанного события
     * - isEdited маппится на поле edited
     * - isDeleted НЕ включается в DTO (обычным пользователям не показываем)
     */
    @Mappings({
            @Mapping(source = "event.id", target = "eventId"),
            @Mapping(source = "edited", target = "isEdited")
    })
    CommentDto toDto(Comment comment);

    /**
     * Преобразует список сущностей в список DTO для пользователей
     */
    List<CommentDto> toDtoList(List<Comment> comments);

    // =========================================================================
    // ПРЕОБРАЗОВАНИЕ Comment → CommentAdminDto (для административного API)
    // =========================================================================

    /**
     * Преобразует сущность в DTO для администраторов
     * - Включает поле isDeleted (администраторы видят удалённые комментарии)
     * - Позволяет модерировать и восстанавливать комментарии
     */
    @Mappings({
            @Mapping(source = "event.id", target = "eventId"),
            @Mapping(source = "edited", target = "isEdited"),
            @Mapping(source = "deleted", target = "isDeleted")
    })
    CommentAdminDto toAdminDto(Comment comment);

    /**
     * Преобразует список сущностей в список DTO для администраторов
     */
    List<CommentAdminDto> toAdminDtoList(List<Comment> comments);
}