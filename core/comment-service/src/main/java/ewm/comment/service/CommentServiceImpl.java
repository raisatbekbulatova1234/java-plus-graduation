package ewm.comment.service;

import ewm.comment.dto.NewCommentDto;
import ewm.comment.dto.UpdateCommentRequest;
import ewm.comment.mapper.CommentMapper;
import ewm.comment.model.Comment;
import ewm.comment.model.CommentStatus;
import ewm.comment.repository.CommentRepository;
import ewm.common.dto.comment.CommentDto;
import ewm.common.exception.ConflictException;
import ewm.common.exception.NotFoundException;
import ewm.common.model.EventState;
import ewm.event.client.EventClient;
import ewm.event.client.dto.EventInternalDto;
import ewm.user.client.UserClient;
import ewm.common.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Creating new comment for user id: {} and event id: {}", userId, eventId);

        UserDto user = userClient.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new NotFoundException("User not found: " + userId);
                });

        EventInternalDto event = eventClient.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id: {}", eventId);
                    return new NotFoundException("Event not found: " + eventId);
                });

        if (!EventState.PUBLISHED.name().equals(event.getState())) {
            log.warn("Attempt to add comment to unpublished event id: {}, state: {}", eventId, event.getState());
            throw new ConflictException("Comments can only be added to published events");
        }

        Comment comment = CommentMapper.mapToComment(newCommentDto);
        comment.setAuthorId(user.getId());
        comment.setEventId(eventId);
        comment.setStatus(CommentStatus.NEW);

        Comment saved = commentRepository.save(comment);
        log.info("Comment created successfully with id: {}", saved.getId());

        return CommentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CommentDto update(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        log.info("Updating comment id: {} for user id: {}", commentId, userId);

        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> {
                    log.error("Comment not found with id: {} for user: {}", commentId, userId);
                    return new NotFoundException("Comment not found: " + commentId);
                });

        if (comment.getStatus() == CommentStatus.REJECTED) {
            log.warn("Attempt to update rejected comment id: {}", commentId);
            throw new ConflictException("Cannot update rejected comment");
        }

        CommentMapper.updateComment(comment, updateCommentRequest);
        comment.setStatus(CommentStatus.NEW); // Reset to NEW when updated

        log.info("Comment id: {} updated successfully", commentId);

        return CommentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        log.debug("Getting comments for event id: {}, from: {}, size: {}", eventId, from, size);

        eventClient.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id: {}", eventId);
                    return new NotFoundException("Event not found: " + eventId);
                });

        Pageable page = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findByEventIdAndStatus(eventId, CommentStatus.APPROVED, page);

        log.debug("Found {} approved comments for event id: {}", comments.size(), eventId);

        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(int from, int size) {
        log.debug("Getting all approved comments, from: {}, size: {}", from, size);

        Pageable page = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByStatus(CommentStatus.APPROVED, page);

        log.debug("Found {} approved comments", comments.size());

        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        log.info("Deleting comment id: {} for user id: {}", commentId, userId);

        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> {
                    log.error("Comment not found with id: {} for user: {}", commentId, userId);
                    return new NotFoundException("Comment not found: " + commentId);
                });

        commentRepository.delete(comment);
        log.info("Comment id: {} deleted successfully", commentId);
    }

    @Override
    @Transactional
    public CommentDto approve(Long commentId) {
        log.info("Approving comment id: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found with id: {}", commentId);
                    return new NotFoundException("Comment not found: " + commentId);
                });

        comment.setStatus(CommentStatus.APPROVED);
        log.info("Comment id: {} approved successfully", commentId);

        return CommentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto reject(Long commentId) {
        log.info("Rejecting comment id: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found with id: {}", commentId);
                    return new NotFoundException("Comment not found: " + commentId);
                });

        comment.setStatus(CommentStatus.REJECTED);
        log.info("Comment id: {} rejected successfully", commentId);

        return CommentMapper.toDto(comment);
    }
}