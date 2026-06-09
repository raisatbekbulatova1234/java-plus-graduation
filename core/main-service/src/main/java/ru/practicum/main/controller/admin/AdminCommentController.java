package ru.practicum.main.controller.admin;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.CommentAdminDto;
import ru.practicum.main.service.CommentService;
import ru.practicum.main.service.params.AdminCommentSearchParams;

/**
 * ============================================================================
 * АДМИНИСТРАТИВНЫЙ КОНТРОЛЛЕР КОММЕНТАРИЕВ
 * ============================================================================
 *
 * Обрабатывает запросы от администраторов для управления комментариями.
 * Позволяет удалять, восстанавливать и просматривать комментарии (включая удалённые).
 */
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCommentController {

    private final CommentService commentService;

    /**
     * Мягкое удаление комментария администратором.
     * Комментарий помечается флагом isDeleted = true.
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long commentId) {
        log.info("Админ: Получен запрос на удаление комментария с ID: {}", commentId);
        commentService.deleteCommentByAdmin(commentId);
        log.info("Админ: Комментарий с ID {} помечен как удалённый", commentId);
    }

    /**
     * Восстанавливает мягко удалённый комментарий.
     * Флаг isDeleted устанавливается в false.
     */
    @PatchMapping("/{commentId}/restore")
    @ResponseStatus(HttpStatus.OK)
    public CommentAdminDto restoreComment(@PathVariable @Positive Long commentId) {
        log.info("Админ: Получен запрос на восстановление комментария с ID: {}", commentId);
        CommentAdminDto restoredComment = commentService.restoreCommentByAdmin(commentId);
        log.info("Админ: Комментарий с ID {} успешно восстановлен", commentId);
        return restoredComment;
    }

    /**
     * Получение списка всех комментариев с возможностью фильтрации.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentAdminDto> getAllCommentsAdmin(
            @RequestParam(name = "userId", required = false) @Positive Long userId,
            @RequestParam(name = "eventId", required = false) @Positive Long eventId,
            @RequestParam(name = "isDeleted", required = false) Boolean isDeleted,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {

        log.info("Админ: Получен запрос на получение комментариев с фильтрами: userId={}, eventId={}, isDeleted={}, from={}, size={}",
                userId, eventId, isDeleted, from, size);

        AdminCommentSearchParams searchParams = AdminCommentSearchParams.builder()
                .userId(userId)
                .eventId(eventId)
                .isDeleted(isDeleted)
                .build();

        List<CommentAdminDto> comments = commentService.getAllCommentsAdmin(searchParams, from, size);

        log.info("Админ: Найдено {} комментариев по заданным критериям", comments.size());
        return comments;
    }
}