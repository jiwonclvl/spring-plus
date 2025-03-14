package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TodoSearchRequest {
    private String title;

    private LocalDate startAt;

    private LocalDate endAt;

    private String nickname;
}
