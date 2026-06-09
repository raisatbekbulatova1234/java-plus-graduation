package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * ============================================================================
 * СУЩНОСТЬ "ПОДБОРКА" (COMPILATION)
 * ============================================================================
 * Представляет подборку событий - тематически сгруппированные события,
 * которые администратор может закрепить на главной странице.
 *
 * - Название подборки уникально
 * - Подборка может содержать множество событий
 * - Одно событие может входить в несколько подборок
 * - Закреплённые подборки отображаются на главной странице
 * ============================================================================
 */
@Entity
@Table(name = "compilations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "events")          // Исключаем events, чтобы избежать циклических ссылок
@EqualsAndHashCode(of = {"id", "title"}) // equals() и hashCode() по id и названию
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Флаг, закреплена ли подборка на главной странице.
     */
    @Column(name = "pinned", nullable = false)
    private boolean pinned;

    /**
     * Название подборки.
     */
    @Column(name = "title", nullable = false, unique = true, length = 128)
    private String title;

    /**
     * События, входящие в подборку.
     *
     * Связь "многие ко многим" (ManyToMany):
     * - Одна подборка может содержать много событий
     * - Одно событие может входить в много подборок
     *
     * @Builder.Default - при создании через Builder создаётся пустой HashSet,
     *                    а не null (избегаем NullPointerException)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events = new HashSet<>();
}