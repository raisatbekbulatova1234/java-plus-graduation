package ru.practicum.main.service;

import ru.practicum.main.dto.EventRequestStatusUpdateResultDto;
import jakarta.validation.constraints.Positive;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.service.params.EventRequestStatusUpdateRequestParams;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(Long userId,Long requestEventId);

    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventRequests(@Positive Long userId, @Positive Long eventId);

    EventRequestStatusUpdateResultDto updateRequestsStatus(EventRequestStatusUpdateRequestParams requestParams);

}
