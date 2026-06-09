package ru.practicum.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ============================================================================
 * КОНФИГУРАЦИЯ JPA АУДИТА
 * ============================================================================
 */
@Configuration           // Обозначает класс как конфигурацию Spring
@EnableJpaAuditing       // Включает JPA Auditing (автоматическое заполнение дат создания/изменения)
@SuppressWarnings("unused")  // Подавляет предупреждения о неиспользуемом классе
public class JpaAuditingConfig {

    // Класс не содержит методов, так как его задача - только включить аудит
    // через аннотацию @EnableJpaAuditing
}