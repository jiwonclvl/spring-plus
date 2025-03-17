package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo qTodo = QTodo.todo;
        QUser qUser = QUser.user;

        return Optional.ofNullable(queryFactory
                .selectFrom(qTodo)
                .leftJoin(qTodo.user,qUser).fetchJoin()
                .where(qTodo.id.eq(todoId))
                .fetchOne());
    }

    @Override
    public Page<TodoSearchResponse> searchTodosByFilters(String title, LocalDateTime startAt, LocalDateTime endAt, String nickname, Pageable pageable) {
        QTodo qTodo = QTodo.todo;
        QManager qManager = QManager.manager;
        QComment qComment = QComment.comment;

        JPQLQuery<Long> manageCount = JPAExpressions
                .select(qManager.count())
                .from(qManager)
                .where(qTodo.id.eq(qManager.todo.id));

        JPQLQuery<Long> commentCount = JPAExpressions
                .select(qComment.count())
                .from(qComment)
                .where(qTodo.id.eq(qComment.todo.id));

        List<TodoSearchResponse> findTodos = queryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        qTodo.title,
                        manageCount,
                        commentCount
                ))
                .from(qTodo)
                .where(
                        titleContains(title),
                        searchNickname(nickname),
                        qTodo.createdAt.between(startAt, endAt)
                )
                .orderBy(qTodo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터의 수 구하기
        JPAQuery<Long> countQuery = queryFactory
                .select(qTodo.count())
                .from(qTodo)
                .where(
                        titleContains(title),
                        searchNickname(nickname),
                        qTodo.createdAt.between(startAt, endAt)
                );

        return PageableExecutionUtils.getPage(findTodos, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? QTodo.todo.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression searchNickname(String nickname) {
        return StringUtils.hasText(nickname) ? QTodo.todo.user.nickname.containsIgnoreCase(nickname) : null;
    }
}
