package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
public class TodoCustomRepositoryImpl implements TodoCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo qTodo = QTodo.todo;
        QUser qUser = QUser.user;

        return Optional.ofNullable(jpaQueryFactory
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


        List<TodoSearchResponse> findTodos = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        qTodo.title,
                        qManager.count(),
                        qComment.count())
                )
                .from(qTodo)
                .leftJoin(qTodo.managers, qManager)
                .leftJoin(qTodo.comments, qComment)
                .groupBy(qTodo)
                .where(
                        searchTitle(title),
                        searchNickname(nickname),
                        qTodo.createdAt.between(startAt, endAt)
                )
                .orderBy(qTodo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터의 수 구하기
        JPAQuery<Todo> countQuery = jpaQueryFactory
                .selectFrom(qTodo)
                .leftJoin(qTodo.managers, qManager)
                .leftJoin(qTodo.comments, qComment)
                .where(
                        searchTitle(title),
                        searchNickname(nickname),
                        qTodo.createdAt.between(startAt, endAt)
                );

        return PageableExecutionUtils.getPage(findTodos, pageable, () -> countQuery.fetch().size());
    }

    private BooleanExpression searchTitle(String title) {
        return StringUtils.hasText(title) ? QTodo.todo.title.likeIgnoreCase("%" + title + "%") : null;
    }

    private BooleanExpression searchNickname(String nickname) {
        return StringUtils.hasText(nickname) ? QTodo.todo.user.nickname.likeIgnoreCase("%" + nickname + "%") : null;
    }
}
