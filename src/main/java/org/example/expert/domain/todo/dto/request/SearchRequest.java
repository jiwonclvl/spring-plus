package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SearchRequest {

    private String weather;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endAt;
}
