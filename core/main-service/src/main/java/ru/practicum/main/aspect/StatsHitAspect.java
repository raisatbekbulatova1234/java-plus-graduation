package ru.practicum.main.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * АОП АСПЕКТ ДЛЯ ЛОГИРОВАНИЯ СТАТИСТИКИ
 * ============================================================================
 *
 * Перехватывает методы, помеченные аннотацией {@link LogStatsHit}, и отправляет
 * информацию о вызове в Сервис Статистики для последующего анализа и подсчёта
 * просмотров и обращений.
 *
 * <p>Используется для отслеживания популярности событий, анализа активности
 * пользователей и сбора метрик использования API.</p>
 * ============================================================================
 */
@Aspect                          // Класс является АОП-аспектом
@Component
@RequiredArgsConstructor
@Slf4j
public class StatsHitAspect {

    /** Клиент для отправки статистики в Сервис Статистики */
    private final StatsClient statsClient;

    /**
     * Имя приложения (берётся из конфигурации, по умолчанию ewm-main-service)
     * Отображается в статистике для идентификации источника вызова.
     * Позволяет различать вызовы от разных микросервисов.
     */
    @Value("${spring.application.name:ewm-main-service}")
    private String appName;

    // =========================================================================
    // СРЕЗ (POINTCUT) - ОПРЕДЕЛЕНИЕ ПЕРЕХВАТЫВАЕМЫХ МЕТОДОВ
    // =========================================================================

    /**
     * Срез для всех методов, помеченных аннотацией @LogStatsHit.
     */
    @Pointcut("@annotation(ru.practicum.main.aspect.LogStatsHit)")
    public void methodsToLogHit() {
        // Пустой метод - точка входа для AspectJ
    }

    // =========================================================================
    // (ADVICE) - ЛОГИКА ПЕРЕХВАТА
    // =========================================================================

    /**
     * ADVICE, выполняющийся ПОСЛЕ успешного выполнения целевого метода
     * (когда метод вернул результат без исключения).
     *
     * <p>Логирует информацию о вызове и отправляет её в Сервис Статистики.</p>
     */
    @AfterReturning(pointcut = "methodsToLogHit()")
    public void logHit(JoinPoint joinPoint) {

        // ---------------------------------------------------------------------
        // 1. ПОЛУЧЕНИЕ HTTP ЗАПРОСА ИЗ КОНТЕКСТА
        // ---------------------------------------------------------------------
        // RequestContextHolder содержит информацию о текущем HTTP запросе
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            // Ситуация: метод вызван вне HTTP контекста (например, из планировщика,
            // очереди сообщений или фоновой задачи)
            log.warn("Невозможно залогировать обращение: HttpServletRequest недоступен в текущем контексте для метода: {}",
                    joinPoint.getSignature().toShortString());
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        // ---------------------------------------------------------------------
        // 2. ИЗВЛЕЧЕНИЕ URI
        // ---------------------------------------------------------------------
        // Получаем путь запроса, например: "/events/42" или "/users/1/subscriptions"
        String uri = request.getRequestURI();

        // ---------------------------------------------------------------------
        // 3. ИЗВЛЕЧЕНИЕ IP-АДРЕСА (С ПОДДЕРЖКОЙ ПРОКСИ)
        // ---------------------------------------------------------------------
        String ip;
        String xRealIp = request.getHeader("X-Real-IP");

        // StringUtils.hasText проверяет: не null, не пустая строка, не только пробелы
        if (StringUtils.hasText(xRealIp)) {
            // За прокси/Nginx настоящий IP клиента передаётся в заголовке X-Real-IP
            ip = xRealIp;
            log.debug("StatsHitAspect: Использован IP из заголовка X-Real-IP: {}", ip);
        } else {
            // Если прокси нет или заголовок не передан - берём IP, который видит сервер
            ip = request.getRemoteAddr();
            log.debug("StatsHitAspect: Заголовок X-Real-IP отсутствует или пуст, использован remoteAddr: {}", ip);
        }

        // ---------------------------------------------------------------------
        // 4. ТЕКУЩЕЕ ВРЕМЯ
        // ---------------------------------------------------------------------
        LocalDateTime timestamp = LocalDateTime.now();

        log.debug("StatsHitAspect: Логирование обращения для приложения='{}', uri='{}', ip='{}'",
                appName, uri, ip);

        // ---------------------------------------------------------------------
        // 5. ФОРМИРОВАНИЕ DTO ДЛЯ ОТПРАВКИ
        // ---------------------------------------------------------------------
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(appName)       // Имя сервиса-источника
                .uri(uri)           // Какой эндпоинт был вызван
                .ip(ip)             // IP клиента
                .timestamp(timestamp) // Когда произошло обращение
                .build();

        // ---------------------------------------------------------------------
        // 6. ОТПРАВКА В СЕРВИС СТАТИСТИКИ (С ОБРАБОТКОЙ ОШИБОК)
        // ---------------------------------------------------------------------
        try {
            // Отправляем данные в stats-service
            statsClient.saveHit(hitDto);
            log.debug("StatsHitAspect: Обращение успешно отправлено в Сервис Статистики для URI: {}", uri);
        } catch (Exception e) {
            // Логируем ошибку, но НЕ бросаем исключение дальше
            // Важно: сбой при сборе статистики НЕ ДОЛЖЕН нарушать основную бизнес-логику.
            // Пользователь не должен увидеть ошибку 500 из-за проблем со статистикой.
            log.error("StatsHitAspect: Не удалось отправить обращение в Сервис Статистики для URI: {}. Ошибка: {}",
                    uri, e.getMessage());
        }
    }
}