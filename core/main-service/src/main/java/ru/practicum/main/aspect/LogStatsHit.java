package ru.practicum.main.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ============================================================================
 * АННОТАЦИЯ @LogStatsHit
 * ============================================================================
 *
 * Маркерная аннотация для AOP-аспекта, который логирует обращение к методам
 * и отправляет статистику в Stats Service.
 *
 * Используется для отслеживания просмотров событий, переходов по ссылкам,
 * API вызовов и других действий, требующих сбора аналитики.
 * ============================================================================
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)   // Доступна во время выполнения (для AOP)
public @interface LogStatsHit {

}