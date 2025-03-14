package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TodoSearchResponse {

    private final String title;

    private final Long countManager;

    private final Long countComment;
}
