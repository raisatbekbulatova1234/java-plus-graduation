package ru.practicum.infra.gateway.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * ============================================================================
 * FALLBACK КОНТРОЛЛЕР ДЛЯ API GATEWAY
 * ============================================================================
 *
 * Обрабатывает запросы, когда целевой сервис (например, main-service)
 * недоступен или Circuit Breaker разомкнут.
 *
 * Используется в настройках gateway:
 * default-filters:
 *   - name: CircuitBreaker
 *     args:
 *       fallbackUri: forward:/service-fallback
 *
 * Возвращает:
 *   - HTTP 200 с JSON-объектом (для совместимости)
 *   - status: "error"
 *   - message: сообщение о недоступности сервиса
 */
@RestController
public class FallbackController {

    /**
     * Fallback-эндпоинт при недоступности целевого сервиса.
     *
     * GET/POST любой запрос на /service-fallback
     *
     * @return Mono с Map, содержащим статус ошибки и сообщение
     */
    @RequestMapping("/service-fallback")
    public Mono<Map<String, String>> serviceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Запрашиваемый сервис временно недоступен. Пожалуйста, попробуйте позже.");
        return Mono.just(response);
    }
}