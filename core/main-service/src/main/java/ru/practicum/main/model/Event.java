package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * ============================================================================
 * СУЩНОСТЬ "СОБЫТИЕ" (EVENT)
 * ============================================================================
 * Представляет событие (мероприятие), созданное пользователем в системе.
 * Является центральной сущностью проекта Explore With Me.
 *
 * Основные характеристики:
 * - Событие создаётся пользователем (инициатором)
 * - Относится к определённой категории
 * - Имеет место проведения (широта/долгота)
 * - Может быть платным или бесплатным
 * - Имеет лимит участников
 * - Проходит процесс модерации (ожидание -> публикация -> отмена)
 * ============================================================================
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"category", "initiator", "compilations"})
@EqualsAndHashCode(of = {"id", "title", "annotation", "eventDate", "publishedOn"})
@EntityListeners(AuditingEntityListener.class)
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткая аннотация события.
     * Отображается в карточке события в списках.
     */
    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    /**
     * Полное описание события.
     * Отображается на отдельной странице события.
     */
    @Column(name = "description", nullable = false, length = 7000)
    private String description;

    /**
     * Дата и время проведения события.
     */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    /**
     * Дата и время создания события.
     * Устанавливается автоматически при первом сохранении.
     */
    @CreatedDate
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    /**
     * Дата и время публикации события.
     * Устанавливается администратором при одобрении.
     * null - событие ещё не опубликовано
     */
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    /**
     * Флаг платного участия.
     * true - участие платное
     * false - участие бесплатное
     */
    @Column(name = "paid", nullable = false)
    private boolean paid;

    /**
     * Лимит участников события.
     * 0 - без ограничений
     * >0 - максимальное количество участников
     */
    @Column(name = "participant_limit", nullable = false)
    private int participantLimit;

    /**
     * Требуется ли модерация заявок на участие.
     * true - заявки требуют подтверждения инициатором
     * false - заявки одобряются автоматически
     */
    @Column(name = "request_moderation", nullable = false)
    private boolean requestModeration;

    /**
     * Заголовок события.
     */
    @Column(name = "title", nullable = false, length = 120)
    private String title;

    /**
     * Категория события.
     * Связь "много к одному" (много событий в одной категории).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Инициатор события (создатель).
     * Связь "много к одному" (много событий у одного пользователя).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    /**
     * Местоположение события (широта и долгота).
     * Встроенное значение (колонки lat, lon в таблице events).
     */
    @Embedded
    private Location location;

    /**
     * Текущее состояние события.
     * Хранится в БД как строка (PENDING, PUBLISHED, CANCELED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private EventState state;

    /**
     * Список подборок, в которых присутствует событие.
     * Обратная сторона связи @ManyToMany (mappedBy = "events").
     * Event является "владельцем" связи через Compilation.events.
     *
     * @Builder.Default - создаётся пустой HashSet, а не null
     */
    @ManyToMany(mappedBy = "events")
    @Builder.Default
    private Set<Compilation> compilations = new HashSet<>();

    /**
     * Количество подтверждённых заявок на участие.
     * Вычисляется автоматически через SQL-формулу при загрузке сущности.
     * Не хранится в БД, вычисляется "на лету".
     */
    @Formula("(SELECT COUNT(r.id) FROM requests r WHERE r.event_id = id AND r.status = 'CONFIRMED')")
    private long confirmedRequestsCount;

    /**
     * Флаг, разрешающий комментарии к событию.
     * true - комментарии разрешены
     * false - комментарии запрещены (или закрыты администратором)
     */
    @Column(name = "comments_enabled", nullable = false)
    private boolean commentsEnabled;
}