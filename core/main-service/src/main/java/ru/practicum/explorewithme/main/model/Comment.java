package ru.practicum.explorewithme.main.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * СУЩНОСТЬ "КОММЕНТАРИЙ" (COMMENT)
 * ============================================================================
 * Представляет комментарий, оставленный пользователем к событию.
 * Позволяет пользователям обсуждать события, делиться мнениями и задавать вопросы.

 * Функциональность:
 *   Пользователи могут оставлять комментарии к событиям
 *   Автор может редактировать свой комментарий
 *   Автор может удалить свой комментарий (мягкое удаление)
 *   Администраторы могут модерировать комментарии
 *   Автоматическое заполнение дат создания и обновления
 *
 * Правила:
 *   Комментарий не может быть пустым (максимум 2000 символов)
 *   Только зарегистрированные пользователи могут оставлять комментарии
 *   Комментарии могут быть оставлены только к опубликованным событиям
 *   При удалении пользователя или события комментарии удаляются каскадно
 * ============================================================================
 */

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"author", "event"}) // Исключаем author и event, чтобы избежать циклических ссылок
@EqualsAndHashCode(of = {"id"})        // equals() и hashCode() только по полю id
@EntityListeners(AuditingEntityListener.class) // Включает автоматическое заполнение полей аудита
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текст комментария.
     * - Обязательное поле (nullable = false)
     * - Максимальная длина: 2000 символов
     */
    @Column(name = "text", nullable = false, length = 2000)
    private String text;

    /**
     * Дата и время создания комментария.
     * Устанавливается автоматически при первом сохранении.
     * Не может быть изменено после создания (updatable = false).
     */
    @CreatedDate
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    /**
     * Дата и время последнего обновления комментария.
     * Устанавливается автоматически при каждом изменении сущности.
     */
    @LastModifiedDate                    // Автоматически заполняется при обновлении
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    /**
     * Автор комментария (связь с пользователем).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //связь "много к одному" (много комментариев у одного пользователя)
    @JoinColumn(name = "author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)//при удалении пользователя все его комментарии удаляются
    private User author;

    /**
     * Событие, к которому относится комментарий (связь с событием).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)//связь "много к одному" (много комментариев у одного события)
    @JoinColumn(name = "event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    /**
     * Флаг, указывающий, был ли комментарий отредактирован.
     */
    @Column(name = "is_edited", nullable = false)
    @Builder.Default                       // Значение по умолчанию при использовании Builder
    private boolean isEdited = false;

    /**
     * Флаг, указывающий, был ли комментарий удалён (мягкое удаление).
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;
}