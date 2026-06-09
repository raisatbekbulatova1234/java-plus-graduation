package ru.practicum.stats.server.error;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.common.error.ApiError;

/**
 * ============================================================================
 * ГЛОБАЛЬНЫЙ ОБРАБОТЧИК ИСКЛЮЧЕНИЙ (STATS-SERVER)
 * ============================================================================
 *
 * Перехватывает исключения в контроллерах сервиса статистики и возвращает
 * единообразный ответ в формате ApiError.
 */
@RestControllerAdvice
@Slf4j
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    /**
     * Ошибка валидации @Valid в теле запроса.
     * Например: @NotNull, @Size и другие.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        String errorMessage = "Ошибка(и) валидации: " + String.join("; ", errors);
        log.warn(errorMessage, e);
        return ApiError.builder()
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST)
                .reason("Некорректный запрос из-за ошибок валидации.")
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Отсутствует обязательный параметр запроса (@RequestParam required=true).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameter(final MissingServletRequestParameterException e) {
        String errorMessage = "Отсутствует обязательный параметр запроса: " + e.getParameterName();
        log.warn(errorMessage, e);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Некорректный запрос.")
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Некорректный аргумент (например, start > end при запросе статистики).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("Некорректный аргумент: {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Некорректный запрос из-за недопустимого аргумента.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Любое другое неперехваченное исключение (ошибка сервера).
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        log.error("Произошла непредвиденная ошибка: {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("На сервере произошла непредвиденная ошибка.")
                .message("Внутренняя ошибка сервера: " + e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}