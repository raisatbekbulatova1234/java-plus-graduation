package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * СУЩНОСТЬ "ЗАПРОС НА УЧАСТИЕ" (PARTICIPATION REQUEST)
 * ============================================================================
 * Представляет запрос от пользователя на участие в событии.
 * Является связующим звеном между пользователем и событием.
 *
 * Правила:
 * - Один пользователь может отправить только один запрос на событие
 * - Если лимит участников достигнут, новые запросы не принимаются
 * - При отмене события все запросы автоматически отменяются
 * - При подтверждении запроса счётчик участников увеличивается
 * ============================================================================
 */
@Entity
@Table(name = "requests", uniqueConstraints = {
        @UniqueConstraint(name = "unique_requester_event", columnNames = {"requester_id", "event_id"})
        // Гарантирует, что один пользователь не может отправить несколько запросов на одно событие
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"requester", "event"})  // Исключаем requester и event для избежания циклических ссылок
@EqualsAndHashCode(of = {"id", "created"})   // equals() и hashCode() по id и дате создания
@EntityListeners(AuditingEntityListener.class) // Включает автоматическое заполнение created
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата и время создания запроса.
     * Устанавливается автоматически при сохранении.
     * Не может быть изменена после создания.
     */
    @CreatedDate
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    /**
     * Пользователь, создавший запрос на участие.
     * Связь "много к одному" (много запросов у одного пользователя).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    /**
     * Событие, на которое пользователь хочет попасть.
     * Связь "много к одному" (много запросов у одного события).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * Статус запроса.
     * Хранится в БД как строка (PENDING, CONFIRMED, REJECTED, CANCELED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status;
}