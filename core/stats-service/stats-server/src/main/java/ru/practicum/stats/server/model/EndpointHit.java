package ru.practicum.stats.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ============================================================================
 * СУЩНОСТЬ "ЗАПИСЬ ОБ ОБРАЩЕНИИ" (ENDPOINT HIT)
 * ============================================================================
 *
 * Хранит информацию о каждом обращении к эндпоинтам системы.
 * Используется для сбора статистики и расчёта просмотров.
 */
@Entity
@Table(name = "endpoint_hits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название сервиса-источника.
     * Примеры: "main-service", "stats-server"
     */
    @Column(name = "app", nullable = false, length = 32)
    private String app;

    /**
     * URI запроса.
     */
    @Column(name = "uri", nullable = false, length = 128)
    private String uri;

    /**
     * IP-адрес клиента (или X-Real-IP при наличии прокси).
     */
    @Column(name = "ip", nullable = false, length = 16)
    private String ip;

    /**
     * Дата и время обращения.
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return id != null && id.equals(((EndpointHit) o).getId());
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : getClass().hashCode();
    }
}