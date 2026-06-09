package ru.practicum.main.controller.admin;

import ru.practicum.main.dto.NewUserRequestDto;
import ru.practicum.main.dto.UserDto;
import ru.practicum.main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.main.service.params.GetListUsersParameters;

/**
 * ============================================================================
 * АДМИНИСТРАТИВНЫЙ КОНТРОЛЛЕР ПОЛЬЗОВАТЕЛЕЙ
 * ============================================================================
 *
 * Обрабатывает запросы от администраторов для управления пользователями.
 * Позволяет создавать, удалять и получать список пользователей.
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminUserController {

    private final UserService userService;

    /**
     * Создание нового пользователя.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequestDto newUserDto) {
        log.info("Админ: Получен запрос на создание пользователя: {}", newUserDto);
        UserDto result = userService.createUser(newUserDto);
        log.info("Админ: Пользователь успешно создан: {}", result);
        return result;
    }

    /**
     * Удаление пользователя по ID.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Админ: Получен запрос на удаление пользователя с ID: {}", userId);
        userService.deleteUser(userId);
        log.info("Админ: Пользователь с ID {} успешно удалён", userId);
    }

    /**
     * Получение списка пользователей с пагинацией.
     * Возможна фильтрация по списку ID пользователей.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Админ: Получен запрос на получение списка пользователей с параметрами: ids {}, from {}, size {}", ids, from, size);
        GetListUsersParameters parameters = GetListUsersParameters.builder()
                .ids(ids)
                .from(from)
                .size(size)
                .build();
        List<UserDto> result = userService.getUsers(parameters);
        log.info("Админ: Получен список пользователей: {}", result);
        return result;
    }
}