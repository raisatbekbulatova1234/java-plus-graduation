package ru.practicum.main.controller.priv;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.service.RequestService;

import java.util.List;

/**
 * ============================================================================
 * ПРИВАТНЫЙ КОНТРОЛЛЕР ЗАПРОСОВ НА УЧАСТИЕ
 * ============================================================================
 *
 * Обрабатывает запросы авторизованных пользователей для управления заявками
 * на участие в событиях.
 */
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateRequestController {

    private final RequestService requestService;

    /**
     * Создание запроса на участие в событии.
     * Правила:
     * - Нельзя подать заявку на своё событие
     * - Нельзя подать заявку на неопубликованное событие
     * - Если лимит участников достигнут - заявка не принимается
     * - Один пользователь - одна заявка на событие
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long eventId) {
        log.info("Приватный: Получен запрос на добавление пользователя {} на событие: {}", userId, eventId);
        ParticipationRequestDto result = requestService.createRequest(userId, eventId);
        log.info("Приватный: Заявка успешно создана: {}", result);
        return result;
    }

    /**
     * Отмена собственного запроса на участие.
     * Статус запроса меняется на CANCELED.
     * Отменить можно только запрос со статусом PENDING.
     */
    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        log.info("Приватный: Получен запрос от пользователя {} на отмену заявки с ID: {}", userId, requestId);
        ParticipationRequestDto result = requestService.cancelRequest(userId, requestId);
        log.info("Приватный: Заявка успешно отменена: {}", result);
        return result;
    }

    /**
     * Получение списка всех своих заявок на участие.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable @Positive Long userId) {
        log.info("Приватный: Получен запрос на получение списка заявок пользователя {}", userId);
        List<ParticipationRequestDto> result = requestService.getRequests(userId);
        log.info("Приватный: Получен список заявок пользователя {}: {} шт.", userId, result.size());
        return result;
    }
}