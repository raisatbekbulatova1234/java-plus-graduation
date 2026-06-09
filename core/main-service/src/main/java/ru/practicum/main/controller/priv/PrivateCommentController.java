package ru.practicum.main.controller.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.dto.UpdateCommentDto;
import ru.practicum.main.service.CommentService;

import java.util.List;

/**
 * ============================================================================
 * ПРИВАТНЫЙ КОНТРОЛЛЕР КОММЕНТАРИЕВ (для пользователей)
 * ============================================================================
 *
 * Обрабатывает запросы авторизованных пользователей для работы с комментариями.
 * Позволяет создавать, редактировать, удалять и просматривать комментарии.
 */
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    /**
     * Создание нового комментария к событию.
     */
    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Создание нового комментария {} пользователем с id {} к событию с id {}",
                newCommentDto, userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(userId, eventId, newCommentDto));
    }

    /**
     * Редактирование своего комментария.
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Обновление комментария c id {} пользователем c id {}, новый текст: {}",
                commentId, userId, updateCommentDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.updateUserComment(userId, commentId, updateCommentDto));
    }

    /**
     * Получение списка всех комментариев пользователя с пагинацией.
     */
    @GetMapping
    public ResponseEntity<List<CommentDto>> getUserComments(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        List<CommentDto> result = commentService.getUserComments(userId, from, size);
        log.info("Получение списка комментариев пользователя c id {} ({} шт.)", userId, result.size());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Мягкое удаление своего комментария.
     * Комментарий помечается флагом isDeleted = true.
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId) {
        log.info("Пользователь id={}: Получен запрос на удаление комментария с ID: {}", userId, commentId);
        commentService.deleteUserComment(userId, commentId);
        log.info("Пользователь id={}: Комментарий с ID {} помечен как удалённый", userId, commentId);
    }
}