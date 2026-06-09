package ru.practicum.explorewithme.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.main.dto.NewUserRequestDto;
import ru.practicum.explorewithme.main.dto.UserDto;
import ru.practicum.explorewithme.main.dto.UserShortDto;
import ru.practicum.explorewithme.main.model.User;

/**
 * MapStruct маппер для сущности User (пользователь)
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует сущность в сокращённый DTO (только id и name)
     * Используется для вложенных объектов (автор комментария, инициатор события)
     */
    UserShortDto toShortDto(User user);

    /**
     * Преобразует сущность в полный DTO (id, name, email)
     */
    UserDto toUserDto(User user);

    /**
     * Преобразует DTO для создания пользователя в сущность
     * - id игнорируется (генерируется БД)
     */
    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequestDto newUserDto);

    /**
     * Преобразует полный DTO в сущность (для обновления)
     */
    User toUser(UserDto userDto);
}