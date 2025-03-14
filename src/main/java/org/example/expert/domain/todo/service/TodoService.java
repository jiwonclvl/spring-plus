package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.SearchRequest;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    /**
     * 일정 등록 비즈니스 로직 수행
     *
     * @param authUser (userId, email, authorities)
     * @param todoSaveRequest (title, contents)
     * @return TodoSaveResponse (id, title, contents, weather, user)
     */
    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    /**
     *
     * @param page (현재 페이지)
     * @param size (페이즈 크기)
     * @param searchRequest (weather, startAt, endAt)
     * @return Page<TodoResponse> (id, title, contents, weather, user, createdAt, modifiedAt)
     */
    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodos(int page, int size, SearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(page - 1, size);


        //입력된 기간의 시작과 끝 날짜 값을 LocalDateTime 타입으로 변환
        LocalDateTime formatStartAt = null;
        LocalDateTime formatEndAt = null;

        //시작 기간이 입력된 경우
        if (!ObjectUtils.isEmpty(searchRequest.getStartAt()) ) {
            formatStartAt = searchRequest.getStartAt().atStartOfDay();
        }
        //끝 기간이 입력된 경우
        if (!ObjectUtils.isEmpty(searchRequest.getEndAt())) {
            formatEndAt = searchRequest.getEndAt().atTime(LocalTime.MAX);
        }

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(searchRequest.getWeather(),formatStartAt, formatEndAt, pageable);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    /**
     * 검색 기능을 통한 일정의 제목, 담당자 수, 댓글 수를 조회하는 로직 수행
     *
     * @param page (현재 페이지)
     * @param size (페이지 사이즈)
     * @param todoSearchRequest (title,startAt, endAt, nickname)
     * @return Page<TodoSearchResponse> (title, countManager, countComment)
     */
    @Transactional(readOnly = true)
    public Page<TodoSearchResponse> searchTodos(int page, int size, TodoSearchRequest todoSearchRequest) {
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDate startAt = todoSearchRequest.getStartAt();
        LocalDate endAt = todoSearchRequest.getEndAt();

        //시작 기간이 입력되지 않은 경우
        if (ObjectUtils.isEmpty(startAt) ) {
            startAt = LocalDate.MIN;
        }
        //끝 기간이 입력되지 않은 경우
        if (ObjectUtils.isEmpty(endAt)) {
            endAt = LocalDate.now();
        }

        LocalDateTime formatStartAt = startAt.atStartOfDay();
        LocalDateTime formatEndAt = endAt.atTime(LocalTime.MAX);

        return todoRepository.searchTodosByFilters(todoSearchRequest.getTitle(), formatStartAt, formatEndAt, todoSearchRequest.getNickname(), pageable);
    }

    /**
     * 일정 단건 조회 로직 수행
     *
     * @param todoId (일정 아이디)
     * @return TodoResponse (id, title, contents, weather, user, createdAt, modifiedAt)
     */
    @Transactional(readOnly = true)
    public TodoResponse getTodo(long todoId) {

        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
}
