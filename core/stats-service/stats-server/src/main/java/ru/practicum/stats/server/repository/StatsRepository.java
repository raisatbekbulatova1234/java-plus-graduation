package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * ============================================================================
 * РЕПОЗИТОРИЙ ДЛЯ СТАТИСТИКИ
 * ============================================================================
 *
 * Обеспечивает доступ к данным об обращениях к эндпоинтам.
 */
@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    /**
     * Получение статистики с группировкой по приложению и URI.
     * Считает общее количество обращений (COUNT(ip)).
     */
    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR eh.uri IN :uris) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStatsDto> findStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") Collection<String> uris);

    /**
     * Получение статистики с группировкой по приложению и URI.
     * Считает количество уникальных IP (COUNT(DISTINCT ip)).
     */
    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR eh.uri IN :uris) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC")
    List<ViewStatsDto> findUniqueStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") Collection<String> uris);
}