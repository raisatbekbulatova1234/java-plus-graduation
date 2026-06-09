package ru.practicum.explorewithme.main.aspect;

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
import ru.practicum.explorewithme.stats.client.StatsClient;
import ru.practicum.explorewithme.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;

/**
 * Аспект для автоматического логирования обращений к методам, помеченным аннотацией @LogStatsHit
 * Отправляет статистику о запросах в сервис статистики
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class StatsHitAspect {

    private final StatsClient statsClient;

    /**
     * Имя приложения, подставляется из конфигурации (по умолчанию ewm-main-service)
     */
    @Value("${spring.application.name:ewm-main-service}")
    private String appName;

    /**
     * Срез (pointcut) для методов, помеченных аннотацией @LogStatsHit
     */
    @Pointcut("@annotation(LogStatsHit)")
    public void methodsToLogHit() {
    }

    /**
     * Совет (advice), выполняющийся после успешного возврата из методов, отмеченных срезом
     * Логирует обращение к эндпоинту и отправляет данные в сервис статистики
     *
     * @param joinPoint точка соединения (информация о вызванном методе)
     */
    @AfterReturning(pointcut = "methodsToLogHit()")
    public void logHit(JoinPoint joinPoint) {
        // Получаем атрибуты HTTP-запроса из текущего контекста
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("Невозможно залогировать обращение: HttpServletRequest недоступен в текущем контексте для метода: {}",
                    joinPoint.getSignature().toShortString());
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        // Получаем URI запроса
        String uri = request.getRequestURI();

        // Определяем IP-адрес: сначала пробуем взять из заголовка X-Real-IP, затем из remoteAddr
        String ip;
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) { // StringUtils.hasText проверяет на null, пустую строку и строку из пробелов
            ip = xRealIp;
            log.debug("StatsHitAspect: Использование IP из заголовка X-Real-IP: {}", ip);
        } else {
            ip = request.getRemoteAddr();
            log.debug("StatsHitAspect: Заголовок X-Real-IP отсутствует или пуст, используется remoteAddr: {}", ip);
        }

        // Время обращения
        LocalDateTime timestamp = LocalDateTime.now();

        log.debug("StatsHitAspect: Логирование обращения для app='{}', uri='{}', ip='{}'", appName, uri, ip);

        // Формируем DTO с данными об обращении
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build();

        // Отправляем данные в сервис статистики
        try {
            statsClient.saveHit(hitDto);
            log.debug("StatsHitAspect: Обращение успешно отправлено в сервис статистики для URI: {}", uri);
        } catch (Exception e) {
            log.error("StatsHitAspect: Не удалось отправить обращение в сервис статистики для URI: {}. Ошибка: {}", uri, e.getMessage());
        }
    }
}